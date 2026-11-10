package com.panbu.app.feature.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.repository.JournalRepository
import com.panbu.app.domain.model.JournalEntry
import com.panbu.app.domain.model.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- Lista ---

data class JournalUiState(
    /** null mientras se lee y descifra. */
    val entries: List<JournalEntry>? = null,
)

@HiltViewModel
class JournalViewModel @Inject constructor(repository: JournalRepository) : ViewModel() {
    val uiState: StateFlow<JournalUiState> = repository.observeAll()
        .map { JournalUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JournalUiState())
}

// --- Escribir / editar ---

data class JournalEditorUiState(
    val entryId: Long = 0,
    val loading: Boolean = false,
    val createdAt: Instant? = null,
    val mood: Mood? = null,
    val text: String = "",
    /** Lo último guardado, para saber si hay cambios sin guardar. */
    val savedMood: Mood? = null,
    val savedText: String = "",
    /** La entrada existe pero no se pudo descifrar: solo se puede borrar. */
    val unreadable: Boolean = false,
    /** true cuando hay que volver a la lista (después de guardar o borrar). */
    val closed: Boolean = false,
) {
    val isNew: Boolean get() = entryId == 0L
    val hasChanges: Boolean get() = text != savedText || mood != savedMood
    val canSave: Boolean get() = text.isNotBlank() && hasChanges && !unreadable
}

/**
 * Editor de una entrada. **El texto no se guarda en el SavedStateHandle** a propósito: ese estado lo conserva
 * Android fuera de la app y sin cifrar (ADR-0006). Si Android cierra la app en segundo plano, se pierde lo que no
 * se guardó; el diálogo "¿Salir sin guardar?" evita perderlo al salir con el botón atrás.
 */
@HiltViewModel
class JournalEditorViewModel @Inject constructor(
    private val repository: JournalRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<Long>(ENTRY_ID_ARG) ?: 0L

    private val _uiState = MutableStateFlow(JournalEditorUiState(entryId = entryId, loading = entryId != 0L))
    val uiState: StateFlow<JournalEditorUiState> = _uiState.asStateFlow()

    init {
        if (entryId != 0L) {
            viewModelScope.launch {
                val entry = repository.get(entryId)
                _uiState.update {
                    if (entry == null) {
                        it.copy(loading = false, closed = true) // ya no existe (por ejemplo, se borró)
                    } else {
                        it.copy(
                            loading = false,
                            createdAt = entry.createdAt,
                            mood = entry.mood,
                            savedMood = entry.mood,
                            text = entry.text.orEmpty(),
                            savedText = entry.text.orEmpty(),
                            unreadable = entry.text == null,
                        )
                    }
                }
            }
        }
    }

    fun onTextChange(text: String) = _uiState.update { it.copy(text = text.take(JournalEntry.MAX_LENGTH)) }

    /** Tocar el estado elegido otra vez lo quita (es opcional). */
    fun onMoodToggle(mood: Mood) = _uiState.update { it.copy(mood = if (it.mood == mood) null else mood) }

    fun onSave() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            repository.save(state.entryId, state.mood, state.text)
            _uiState.update { it.copy(closed = true) }
        }
    }

    fun onDelete() {
        if (entryId == 0L) return
        viewModelScope.launch {
            repository.delete(entryId)
            _uiState.update { it.copy(closed = true) }
        }
    }

    companion object {
        /** Nombre del argumento de [com.panbu.app.navigation.JournalEditorRoute]. */
        const val ENTRY_ID_ARG = "entryId"
    }
}

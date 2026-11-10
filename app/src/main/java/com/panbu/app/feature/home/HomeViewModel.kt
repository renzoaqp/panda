package com.panbu.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.repository.MoodRepository
import com.panbu.app.domain.model.Mood
import com.panbu.app.domain.model.MoodEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    /** Último estado de ánimo registrado HOY, o null si todavía no hay registro. */
    val todayMood: Mood? = null,
)

/**
 * ViewModel de Inicio. Es el ejemplo de referencia del patrón del proyecto:
 * repositorio → Flow → StateFlow<UiState> → pantalla.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val clock: Clock,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = moodRepository.observeLatest()
        .map { latest -> HomeUiState(todayMood = latest?.takeIf { it.isFromToday() }?.mood) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun onMoodSelected(mood: Mood) {
        // Tocar otra vez el mismo estado no crea un registro duplicado (docs/02-producto.md, Inicio).
        if (uiState.value.todayMood == mood) return
        viewModelScope.launch { moodRepository.record(mood) }
    }

    private fun MoodEntry.isFromToday(): Boolean =
        createdAt.atZone(clock.zone).toLocalDate() == LocalDate.now(clock)
}

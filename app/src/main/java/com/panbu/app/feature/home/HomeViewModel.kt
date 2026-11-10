package com.panbu.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.preferences.PreferencesRepository
import com.panbu.app.data.repository.MoodRepository
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.model.Mood
import com.panbu.app.domain.model.MoodEntry
import com.panbu.app.domain.usecase.ObserveTodayProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    /** Nombre para el saludo ("Hola, Ana"); null si la persona no lo escribió. */
    val userName: String? = null,
    /** Último estado de ánimo registrado HOY, o null si todavía no hay registro. */
    val todayMood: Mood? = null,
    val calmToday: Duration = Duration.ZERO,
    val calmYesterday: Duration = Duration.ZERO,
    val goals: List<GoalItem> = emptyList(),
)

/**
 * ViewModel de Inicio. Es el ejemplo de referencia del patrón del proyecto:
 * repositorios → Flow → StateFlow<UiState> → pantalla.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    observeTodayProgress: ObserveTodayProgress,
    preferences: PreferencesRepository,
    private val clock: Clock,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        moodRepository.observeLatest(),
        observeTodayProgress(),
        preferences.preferences,
    ) { latestMood, today, prefs ->
        HomeUiState(
            userName = prefs.displayName,
            todayMood = latestMood?.takeIf { it.isFromToday() }?.mood,
            calmToday = today.calmToday,
            calmYesterday = today.calmYesterday,
            goals = today.goals,
        )
    }.stateIn(
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

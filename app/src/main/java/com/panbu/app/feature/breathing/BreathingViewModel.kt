package com.panbu.app.feature.breathing

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.R
import com.panbu.app.data.repository.CalmSessionRepository
import com.panbu.app.di.ApplicationScope
import com.panbu.app.domain.model.BreathingPattern
import com.panbu.app.domain.model.BreathingStep
import com.panbu.app.domain.model.CalmActivityType
import com.panbu.app.domain.model.CalmSession
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class BreathingPreset(val pattern: BreathingPattern, @StringRes val labelRes: Int) {
    GENTLE(BreathingPattern.Gentle, R.string.breathing_pattern_gentle),
    BOX(BreathingPattern.Box, R.string.breathing_pattern_box),
    RELAX_478(BreathingPattern.Relax478, R.string.breathing_pattern_478),
}

enum class BreathingStage { SETUP, RUNNING, FINISHED }

data class BreathingUiState(
    val stage: BreathingStage = BreathingStage.SETUP,
    val preset: BreathingPreset = BreathingPreset.GENTLE,
    val minutes: Int = 1,
    val elapsedMillis: Long = 0,
    val step: BreathingStep? = null,
    val finishedDuration: Duration = Duration.ZERO,
) {
    val totalMillis: Long get() = minutes * 60_000L
    val remainingSeconds: Long get() = ((totalMillis - elapsedMillis).coerceAtLeast(0) + 999) / 1000
    val progress: Float get() = if (totalMillis == 0L) 0f else elapsedMillis.toFloat() / totalMillis
    val finishedCounts: Boolean get() = finishedDuration >= CalmSession.MIN_COUNTED_DURATION

    companion object {
        val MINUTE_OPTIONS = listOf(1, 3, 5)
    }
}

/**
 * Respira con Panbu (docs/02-producto.md §4). Al terminar, o si la persona sale antes, guarda una sesión de
 * respiración (solo si duró al menos 30 s), que suma al "Tiempo contigo" y a las metas.
 */
@HiltViewModel
class BreathingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calmSessions: CalmSessionRepository,
    private val clock: Clock,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreathingUiState())
    val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()

    private var startedAt: Instant? = null
    private var ticker: Job? = null

    init {
        // "Necesito un momento de calma": 1 minuto de respiración suave, sin pasos previos.
        if (savedStateHandle.get<Boolean>(QUICK_START_ARG) == true) start()
    }

    fun onPresetSelected(preset: BreathingPreset) = _uiState.update { it.copy(preset = preset) }

    fun onMinutesSelected(minutes: Int) = _uiState.update { it.copy(minutes = minutes) }

    fun start() {
        if (startedAt != null) return
        val begin = clock.instant()
        startedAt = begin
        _uiState.update {
            it.copy(stage = BreathingStage.RUNNING, elapsedMillis = 0, step = it.preset.pattern.stepAt(0))
        }
        ticker = viewModelScope.launch {
            while (isActive) {
                val elapsed = Duration.between(begin, clock.instant()).toMillis()
                if (elapsed >= _uiState.value.totalMillis) {
                    finish()
                    break
                }
                _uiState.update { it.copy(elapsedMillis = elapsed, step = it.preset.pattern.stepAt(elapsed)) }
                delay(TICK_MILLIS)
            }
        }
    }

    /** Termina el ejercicio (al completarse, con el botón "Terminar" o al salir de la pantalla). */
    fun finish() {
        val begin = startedAt ?: return
        startedAt = null
        ticker?.cancel()
        ticker = null
        val end = clock.instant()
        // En el scope de la app: la sesión se guarda aunque la pantalla ya se haya cerrado.
        appScope.launch { calmSessions.record(CalmActivityType.BREATHING, begin, end) }
        _uiState.update { it.copy(stage = BreathingStage.FINISHED, finishedDuration = Duration.between(begin, end)) }
    }

    fun again() = _uiState.update { BreathingUiState(preset = it.preset, minutes = it.minutes) }

    override fun onCleared() {
        finish()
    }

    companion object {
        /** Debe llamarse igual que la propiedad de BreathingRoute. */
        const val QUICK_START_ARG = "quickStart"
        private const val TICK_MILLIS = 100L
    }
}

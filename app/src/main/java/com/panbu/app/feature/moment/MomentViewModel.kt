package com.panbu.app.feature.moment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.repository.CalmSessionRepository
import com.panbu.app.di.ApplicationScope
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

enum class MomentStage { IDLE, RUNNING, FINISHED }

data class MomentUiState(
    val stage: MomentStage = MomentStage.IDLE,
    val elapsedSeconds: Long = 0,
    val finishedDuration: Duration = Duration.ZERO,
) {
    val finishedCounts: Boolean get() = finishedDuration >= CalmSession.MIN_COUNTED_DURATION
}

/**
 * Un momento con Panbu: temporizador del "Tiempo contigo" hasta que el peluche lo registre solo (Fase 5).
 * Se corta solo a las 2 horas, para que un temporizador olvidado no infle los datos.
 */
@HiltViewModel
class MomentViewModel @Inject constructor(
    private val calmSessions: CalmSessionRepository,
    private val clock: Clock,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MomentUiState())
    val uiState: StateFlow<MomentUiState> = _uiState.asStateFlow()

    private var startedAt: Instant? = null
    private var ticker: Job? = null

    fun start() {
        if (startedAt != null) return
        val begin = clock.instant()
        startedAt = begin
        _uiState.value = MomentUiState(stage = MomentStage.RUNNING)
        ticker = viewModelScope.launch {
            while (isActive) {
                val elapsed = Duration.between(begin, clock.instant())
                if (elapsed >= MAX_DURATION) {
                    finish()
                    break
                }
                _uiState.update { it.copy(elapsedSeconds = elapsed.seconds) }
                delay(TICK_MILLIS)
            }
        }
    }

    fun finish() {
        val begin = startedAt ?: return
        startedAt = null
        ticker?.cancel()
        ticker = null
        val end = minOf(clock.instant(), begin.plus(MAX_DURATION))
        appScope.launch { calmSessions.record(CalmActivityType.MOMENT_WITH_PANBU, begin, end) }
        _uiState.update { it.copy(stage = MomentStage.FINISHED, finishedDuration = Duration.between(begin, end)) }
    }

    override fun onCleared() {
        finish()
    }

    companion object {
        val MAX_DURATION: Duration = Duration.ofHours(2)
        private const val TICK_MILLIS = 500L
    }
}

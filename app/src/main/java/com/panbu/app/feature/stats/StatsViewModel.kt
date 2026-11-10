package com.panbu.app.feature.stats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.domain.model.PeriodStats
import com.panbu.app.domain.model.StatsPeriod
import com.panbu.app.domain.usecase.ObserveStats
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val period: StatsPeriod = StatsPeriod.WEEK,
    /** null mientras se lee la base de datos. */
    val stats: PeriodStats? = null,
)

/** Estadísticas (docs/02-producto.md §2): el periodo elegido y sus números, siempre con datos reales. */
@HiltViewModel
class StatsViewModel @Inject constructor(
    observeStats: ObserveStats,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // El periodo elegido sobrevive a la rotación y a que Android cierre la app en segundo plano.
    private val period = savedStateHandle.getStateFlow(KEY_PERIOD, StatsPeriod.WEEK)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatsUiState> = period
        .flatMapLatest { selected ->
            observeStats(selected)
                .map { StatsUiState(period = selected, stats = it) }
                .onStart { emit(StatsUiState(period = selected)) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState(period = period.value),
        )

    fun onPeriodSelected(selected: StatsPeriod) {
        savedStateHandle[KEY_PERIOD] = selected
    }

    private companion object {
        const val KEY_PERIOD = "period"
    }
}

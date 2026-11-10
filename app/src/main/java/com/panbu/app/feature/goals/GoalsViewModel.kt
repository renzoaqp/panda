package com.panbu.app.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.usecase.ObserveTodayProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** @param goals null mientras carga (para no mostrar el estado vacío un instante). */
data class GoalsUiState(val goals: List<GoalItem>? = null)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    observeTodayProgress: ObserveTodayProgress,
) : ViewModel() {

    val uiState: StateFlow<GoalsUiState> = observeTodayProgress()
        .map { GoalsUiState(goals = it.goals) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GoalsUiState())
}

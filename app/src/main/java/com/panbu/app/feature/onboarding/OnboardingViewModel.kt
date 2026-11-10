package com.panbu.app.feature.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.preferences.PreferencesRepository
import com.panbu.app.domain.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Pasos de la bienvenida (docs/02-producto.md §7). La cuenta opcional se suma en el APK 0.4. */
enum class OnboardingStep { WELCOME, HOW, PRIVACY, NAME, DISCLAIMER }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val name: String = "",
) {
    val isFirst: Boolean get() = step == OnboardingStep.entries.first()
    val isLast: Boolean get() = step == OnboardingStep.entries.last()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferences: PreferencesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Paso y nombre sobreviven a la rotación y a que Android cierre la app en segundo plano.
    private val step = savedStateHandle.getStateFlow(KEY_STEP, OnboardingStep.WELCOME)
    private val name = savedStateHandle.getStateFlow(KEY_NAME, "")

    val uiState: StateFlow<OnboardingUiState> = combine(step, name, ::OnboardingUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OnboardingUiState(step.value, name.value))

    fun onNext() {
        val current = step.value
        if (current == OnboardingStep.entries.last()) return
        savedStateHandle[KEY_STEP] = OnboardingStep.entries[current.ordinal + 1]
    }

    fun onBack() {
        val current = step.value
        if (current == OnboardingStep.entries.first()) return
        savedStateHandle[KEY_STEP] = OnboardingStep.entries[current.ordinal - 1]
    }

    fun onNameChange(value: String) {
        savedStateHandle[KEY_NAME] = value.take(UserPreferences.MAX_NAME_LENGTH)
    }

    /** Guarda el nombre y marca la bienvenida como vista: la app pasa sola a Inicio. */
    fun onFinish() {
        viewModelScope.launch { preferences.completeOnboarding(name.value) }
    }

    private companion object {
        const val KEY_STEP = "step"
        const val KEY_NAME = "name"
    }
}

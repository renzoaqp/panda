package com.panbu.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Qué se ve al abrir la app: la bienvenida (primera vez o después de borrar todo) o las pestañas. */
enum class AppStart { LOADING, ONBOARDING, MAIN }

@HiltViewModel
class AppViewModel @Inject constructor(preferences: PreferencesRepository) : ViewModel() {

    val start: StateFlow<AppStart> = preferences.preferences
        .map { if (it.onboardingDone) AppStart.MAIN else AppStart.ONBOARDING }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppStart.LOADING)
}

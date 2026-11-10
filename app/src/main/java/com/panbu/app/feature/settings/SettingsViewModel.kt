package com.panbu.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panbu.app.data.LocalDataEraser
import com.panbu.app.data.preferences.PreferencesRepository
import com.panbu.app.di.ApplicationScope
import com.panbu.app.domain.model.UserPreferences
import com.panbu.app.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Ajustes (docs/02-producto.md §6): perfil, horario de descanso, recordatorio y "Borrar todos mis datos". */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: PreferencesRepository,
    private val reminders: ReminderScheduler,
    private val eraser: LocalDataEraser,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    /** null mientras se leen las preferencias. */
    val uiState: StateFlow<UserPreferences?> = preferences.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onNameChange(name: String) {
        viewModelScope.launch { preferences.setUserName(name) }
    }

    fun onRemindersChange(enabled: Boolean) = updateReminder { setNotificationsEnabled(enabled) }

    fun onRestScheduleChange(start: LocalTime, end: LocalTime) = updateReminder { setRestSchedule(start, end) }

    fun onReminderMinutesChange(minutes: Int) = updateReminder { setReminderMinutesBefore(minutes) }

    /**
     * Borra todo. Corre en el scope de la app y no en el de la pantalla: al terminar, la app vuelve a la
     * bienvenida y esta pantalla se cierra, pero el borrado tiene que terminar igual.
     */
    fun onEraseAll() {
        appScope.launch { eraser.eraseAll() }
    }

    /** Guarda el cambio y vuelve a programar (o cancela) el recordatorio con los valores nuevos. */
    private fun updateReminder(change: suspend PreferencesRepository.() -> Unit) {
        viewModelScope.launch {
            preferences.change()
            reminders.sync(preferences.preferences.first())
        }
    }
}

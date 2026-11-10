package com.panbu.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.panbu.app.domain.model.UserPreferences
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Preferencias de la persona: nombre, bienvenida, recordatorios y horario de descanso. */
interface PreferencesRepository {
    val preferences: Flow<UserPreferences>

    /** Termina la bienvenida (con el nombre, si lo escribió). */
    suspend fun completeOnboarding(userName: String?)

    suspend fun setUserName(userName: String?)

    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun setRestSchedule(start: LocalTime, end: LocalTime)

    suspend fun setReminderMinutesBefore(minutes: Int)

    /** Borra todas las preferencias (parte de "Borrar todos mis datos"). */
    suspend fun clear()
}

/**
 * Implementación con DataStore. Las horas se guardan como minutos desde la medianoche (22:00 → 1320).
 * Los nombres de las claves no se cambian después de publicar: se perderían las preferencias guardadas.
 */
class DataStorePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {

    override val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            userName = prefs[Keys.USER_NAME],
            onboardingDone = prefs[Keys.ONBOARDING_DONE] ?: false,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: false,
            restStart = prefs[Keys.REST_START]?.toLocalTime() ?: UserPreferences.DEFAULT_REST_START,
            restEnd = prefs[Keys.REST_END]?.toLocalTime() ?: UserPreferences.DEFAULT_REST_END,
            reminderMinutesBefore = prefs[Keys.REMINDER_MINUTES_BEFORE] ?: UserPreferences.DEFAULT_REMINDER_MINUTES,
        )
    }

    override suspend fun completeOnboarding(userName: String?) {
        dataStore.edit { prefs ->
            prefs.setName(userName)
            prefs[Keys.ONBOARDING_DONE] = true
        }
    }

    override suspend fun setUserName(userName: String?) {
        dataStore.edit { prefs -> prefs.setName(userName) }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override suspend fun setRestSchedule(start: LocalTime, end: LocalTime) {
        dataStore.edit {
            it[Keys.REST_START] = start.toMinuteOfDay()
            it[Keys.REST_END] = end.toMinuteOfDay()
        }
    }

    override suspend fun setReminderMinutesBefore(minutes: Int) {
        dataStore.edit { it[Keys.REMINDER_MINUTES_BEFORE] = minutes }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private fun androidx.datastore.preferences.core.MutablePreferences.setName(userName: String?) {
        val clean = userName?.trim()?.take(UserPreferences.MAX_NAME_LENGTH)
        if (clean.isNullOrEmpty()) remove(Keys.USER_NAME) else this[Keys.USER_NAME] = clean
    }

    private object Keys {
        val USER_NAME = stringPreferencesKey("userName")
        val ONBOARDING_DONE = booleanPreferencesKey("onboardingDone")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notificationsEnabled")
        val REST_START = intPreferencesKey("restStart")
        val REST_END = intPreferencesKey("restEnd")
        val REMINDER_MINUTES_BEFORE = intPreferencesKey("reminderMinutesBefore")
    }
}

private fun Int.toLocalTime(): LocalTime = LocalTime.of((this / 60) % 24, this % 60)

private fun LocalTime.toMinuteOfDay(): Int = hour * 60 + minute

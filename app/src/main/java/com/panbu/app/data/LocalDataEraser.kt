package com.panbu.app.data

import com.panbu.app.data.local.PanbuDatabase
import com.panbu.app.data.preferences.PreferencesRepository
import com.panbu.app.data.repository.GoalRepository
import com.panbu.app.domain.model.UserPreferences
import com.panbu.app.reminders.ReminderScheduler
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * "Borrar todos mis datos" (Ajustes › Privacidad y mis datos; docs/06-guardrails.md).
 *
 * Borra todo lo que Panbu guardó en el celular: la base de datos (ánimo, sesiones, metas, diario), las
 * preferencias y el recordatorio programado. Al borrar las preferencias, la app vuelve a la bienvenida.
 */
class LocalDataEraser @Inject constructor(
    private val database: PanbuDatabase,
    private val preferences: PreferencesRepository,
    private val goals: GoalRepository,
    private val reminders: ReminderScheduler,
) {
    suspend fun eraseAll() = withContext(Dispatchers.IO) {
        reminders.sync(UserPreferences()) // con los valores por defecto el recordatorio está apagado: se cancela
        database.clearAllTables()
        goals.ensureDefaultGoals() // la app vuelve a empezar con sus metas iniciales
        preferences.clear() // al final: esto muestra la bienvenida otra vez
    }
}

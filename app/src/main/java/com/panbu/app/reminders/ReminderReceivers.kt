package com.panbu.app.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.panbu.app.data.preferences.PreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Acceso a Hilt desde los receptores. Se usa un EntryPoint en vez de @AndroidEntryPoint porque en Kotlin
 * no se puede llamar a `super.onReceive` (es abstracto) y Hilt lo necesitaría para inyectar.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderEntryPoint {
    fun preferences(): PreferencesRepository
    fun scheduler(): ReminderScheduler
    fun notifier(): ReminderNotifier
}

private fun Context.reminderEntryPoint(): ReminderEntryPoint =
    EntryPointAccessors.fromApplication(applicationContext, ReminderEntryPoint::class.java)

/** Corre [block] fuera del hilo principal y avisa a Android cuando termina (goAsync). */
private fun BroadcastReceiver.runAsync(block: suspend () -> Unit) {
    val pending = goAsync()
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
        try {
            block()
        } finally {
            pending.finish()
        }
    }
}

/** Llega la hora: muestra el recordatorio (si sigue activado) y programa el de mañana. */
class RestReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val entryPoint = context.reminderEntryPoint()
        runAsync {
            val preferences = entryPoint.preferences().preferences.first()
            if (preferences.notificationsEnabled) entryPoint.notifier().showRestReminder(preferences)
            entryPoint.scheduler().sync(preferences)
        }
    }
}

/**
 * Vuelve a programar el recordatorio cuando Android borra las alarmas o cambia la hora: al reiniciar el
 * celular, al actualizar la app y al cambiar la hora o la zona horaria.
 */
class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in HANDLED_ACTIONS) return
        val entryPoint = context.reminderEntryPoint()
        runAsync { entryPoint.scheduler().sync(entryPoint.preferences().preferences.first()) }
    }

    private companion object {
        val HANDLED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}

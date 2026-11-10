package com.panbu.app.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.panbu.app.domain.model.RestReminder
import com.panbu.app.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

/** Programa (o cancela) el recordatorio diario para dejar el celular según las preferencias. */
interface ReminderScheduler {
    /** Si el recordatorio está activado, programa el próximo; si no, cancela el que hubiera. */
    fun sync(preferences: UserPreferences)
}

/**
 * Implementación con AlarmManager y una ventana de 10 minutos (alarma inexacta): no necesita el permiso
 * SCHEDULE_EXACT_ALARM, y unos minutos de diferencia no importan (docs/decisiones/0005-recordatorios-con-alarmmanager.md).
 * Android borra las alarmas al reiniciar el celular: [RescheduleReceiver] las vuelve a programar.
 */
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clock: Clock,
) : ReminderScheduler {

    override fun sync(preferences: UserPreferences) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, RestReminderReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        if (!preferences.notificationsEnabled) {
            alarmManager.cancel(pendingIntent)
            return
        }
        // La zona se lee en cada llamada: si la persona viaja o cambia la hora, el aviso sigue su hora local.
        val next = RestReminder.nextAt(
            now = ZonedDateTime.now(clock.withZone(ZoneId.systemDefault())),
            restStart = preferences.restStart,
            minutesBefore = preferences.reminderMinutesBefore,
        )
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, next.toInstant().toEpochMilli(), WINDOW_MILLIS, pendingIntent)
    }

    private companion object {
        const val REQUEST_CODE = 2201
        const val WINDOW_MILLIS = 10 * 60 * 1000L
    }
}

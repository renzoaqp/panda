package com.panbu.app.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.panbu.app.MainActivity
import com.panbu.app.R
import com.panbu.app.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/** true si Android deja mostrar notificaciones de Panbu (permiso en Android 13+ y notificaciones activadas). */
fun Context.canPostNotifications(): Boolean {
    val permissionOk = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
    return permissionOk && NotificationManagerCompat.from(this).areNotificationsEnabled()
}

/** true si hay que pedir el permiso de notificaciones (Android 13+ y todavía no se concedió). */
fun Context.needsNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED

/**
 * Muestra la notificación del recordatorio de descanso.
 *
 * Reglas (docs/06-guardrails.md): como máximo 2 notificaciones al día (hoy solo existe esta, una vez al día),
 * sin culpa ni presión, y **nunca** con la tienda. Al tocarla se abre la app.
 */
class ReminderNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun showRestReminder(preferences: UserPreferences) {
        if (!context.canPostNotifications()) return
        ensureChannel()
        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val pattern = if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm a"
        val restStart = preferences.restStart.format(DateTimeFormatter.ofPattern(pattern, Locale.forLanguageTag("es")))
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_moon)
            .setContentTitle(context.getString(R.string.reminder_title))
            .setContentText(context.getString(R.string.reminder_text, restStart))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.reminder_text, restStart)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // El permiso se quitó justo ahora: no se muestra y no pasa nada más.
        }
    }

    private fun ensureChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = context.getString(R.string.reminder_channel_desc) }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "rest_reminder"
        const val NOTIFICATION_ID = 2201
    }
}

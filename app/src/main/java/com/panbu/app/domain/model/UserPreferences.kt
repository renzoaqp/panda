package com.panbu.app.domain.model

import java.time.LocalTime

/**
 * Preferencias de la persona (docs/05-modelo-de-datos.md, "Preferencias"). Se guardan solo en el celular
 * (DataStore); no son datos emocionales, pero tampoco salen del celular.
 */
data class UserPreferences(
    /** Nombre para el saludo ("Hola, Ana"). null o vacío = "Hola". */
    val userName: String? = null,
    /** true cuando la persona terminó la bienvenida y vio el aviso de que Panbu no sustituye a un profesional. */
    val onboardingDone: Boolean = false,
    /** Recordatorio para dejar el celular antes del horario de descanso. Apagado hasta que la persona lo activa. */
    val notificationsEnabled: Boolean = false,
    /** Inicio del horario de descanso (por defecto 22:00). */
    val restStart: LocalTime = DEFAULT_REST_START,
    /** Fin del horario de descanso (por defecto 06:00). Puede ser del día siguiente. */
    val restEnd: LocalTime = DEFAULT_REST_END,
    /** Cuántos minutos antes del descanso llega el recordatorio. */
    val reminderMinutesBefore: Int = DEFAULT_REMINDER_MINUTES,
) {
    /** El nombre listo para mostrar, o null si no hay. */
    val displayName: String? get() = userName?.trim()?.takeIf { it.isNotEmpty() }

    companion object {
        val DEFAULT_REST_START: LocalTime = LocalTime.of(22, 0)
        val DEFAULT_REST_END: LocalTime = LocalTime.of(6, 0)
        const val DEFAULT_REMINDER_MINUTES = 30

        /** Opciones de "Avisarme antes" en Ajustes. */
        val REMINDER_OPTIONS = listOf(15, 30, 60)

        /** Largo máximo del nombre (cabe en el saludo de Inicio). */
        const val MAX_NAME_LENGTH = 30
    }
}

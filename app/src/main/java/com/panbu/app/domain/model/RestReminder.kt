package com.panbu.app.domain.model

import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * Recordatorio para dejar el celular (docs/02-producto.md §6): llega una vez al día, unos minutos antes del
 * inicio del horario de descanso. Kotlin puro: recibe "ahora" y devuelve cuándo debe llegar el próximo.
 */
object RestReminder {

    /**
     * Próximo aviso: hoy a la hora `inicio del descanso − minutos`; si esa hora ya pasó (o es justo ahora),
     * mañana a la misma hora. Si el descanso empieza 00:30 y el aviso es 60 min antes, llega a las 23:30.
     */
    fun nextAt(now: ZonedDateTime, restStart: LocalTime, minutesBefore: Int): ZonedDateTime {
        val time = reminderTime(restStart, minutesBefore)
        val today = now.toLocalDate().atTime(time).atZone(now.zone)
        return if (today.isAfter(now)) today else now.toLocalDate().plusDays(1).atTime(time).atZone(now.zone)
    }

    /** La hora del aviso (puede ser del día anterior al inicio del descanso). */
    fun reminderTime(restStart: LocalTime, minutesBefore: Int): LocalTime =
        restStart.minusMinutes(minutesBefore.toLong().coerceAtLeast(0))
}

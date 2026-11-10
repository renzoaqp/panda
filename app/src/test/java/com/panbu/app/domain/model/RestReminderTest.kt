package com.panbu.app.domain.model

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

/** Cuándo llega el recordatorio para dejar el celular (zona de Lima). */
class RestReminderTest {

    private val lima = ZoneId.of("America/Lima")

    private fun at(text: String): ZonedDateTime = LocalDateTime.parse(text).atZone(lima)

    @Test
    fun `arrives today before the rest time if it has not passed yet`() {
        val next = RestReminder.nextAt(at("2026-11-10T18:00:00"), LocalTime.of(22, 0), minutesBefore = 30)
        assertEquals(at("2026-11-10T21:30:00"), next)
    }

    @Test
    fun `arrives tomorrow if today's time already passed`() {
        val next = RestReminder.nextAt(at("2026-11-10T21:45:00"), LocalTime.of(22, 0), minutesBefore = 30)
        assertEquals(at("2026-11-11T21:30:00"), next)
    }

    @Test
    fun `right at the reminder time, the next one is tomorrow (it never repeats the same day)`() {
        val next = RestReminder.nextAt(at("2026-11-10T21:30:00"), LocalTime.of(22, 0), minutesBefore = 30)
        assertEquals(at("2026-11-11T21:30:00"), next)
    }

    @Test
    fun `a rest time after midnight moves the reminder to the night before`() {
        val next = RestReminder.nextAt(at("2026-11-10T20:00:00"), LocalTime.of(0, 30), minutesBefore = 60)
        assertEquals(at("2026-11-10T23:30:00"), next)
    }

    @Test
    fun `negative minutes are treated as zero`() {
        assertEquals(LocalTime.of(22, 0), RestReminder.reminderTime(LocalTime.of(22, 0), minutesBefore = -5))
    }
}

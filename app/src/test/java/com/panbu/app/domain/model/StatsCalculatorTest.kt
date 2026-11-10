package com.panbu.app.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reglas de Estadísticas (docs/05-modelo-de-datos.md). Zona de Lima (UTC-5).
 * Referencia: el martes 10 de noviembre de 2026; su semana va del lunes 9 al domingo 15.
 */
class StatsCalculatorTest {

    private val lima = ZoneId.of("America/Lima")
    private val tuesday = LocalDate.of(2026, 11, 10)

    private fun session(start: String, minutes: Long = 0, seconds: Long = 0) =
        LocalDateTime.parse(start).atZone(lima).toInstant().let { startedAt ->
            CalmSession(
                type = CalmActivityType.BREATHING,
                source = DataSource.MANUAL,
                startedAt = startedAt,
                endedAt = startedAt.plus(Duration.ofMinutes(minutes).plusSeconds(seconds)),
            )
        }

    private fun mood(at: String, mood: Mood) =
        MoodEntry(mood = mood, createdAt = LocalDateTime.parse(at).atZone(lima).toInstant())

    private fun build(
        period: StatsPeriod,
        today: LocalDate = tuesday,
        sessions: List<CalmSession> = emptyList(),
        moods: List<MoodEntry> = emptyList(),
    ) = StatsCalculator.build(period, today, lima, sessions, moods)

    // --- Semana ---

    @Test
    fun `the week goes from Monday to Sunday with one bar per day`() {
        val stats = build(StatsPeriod.WEEK)

        assertEquals(DateSpan(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 16)), stats.span)
        assertEquals(7, stats.bars.size)
        assertEquals(LocalDate.of(2026, 11, 9), stats.bars.first().span.start)
        assertEquals(LocalDate.of(2026, 11, 15), stats.bars.last().span.start)
    }

    @Test
    fun `on Sunday the week is still the one that started on Monday`() {
        val stats = build(StatsPeriod.WEEK, today = LocalDate.of(2026, 11, 15))
        assertEquals(LocalDate.of(2026, 11, 9), stats.span.start)
    }

    @Test
    fun `calm time adds up the sessions of the week and fills each day's bar`() {
        val stats = build(
            StatsPeriod.WEEK,
            sessions = listOf(
                session("2026-11-09T08:00:00", minutes = 10),
                session("2026-11-09T20:00:00", minutes = 5),
                session("2026-11-10T07:00:00", minutes = 20),
                session("2026-11-08T07:00:00", minutes = 60), // domingo anterior: otra semana
            ),
        )

        assertEquals(Duration.ofMinutes(35), stats.calmTotal)
        assertEquals(Duration.ofMinutes(15), stats.bars[0].calm) // lunes
        assertEquals(Duration.ofMinutes(20), stats.bars[1].calm) // martes
        assertEquals(Duration.ZERO, stats.bars[2].calm)
    }

    @Test
    fun `sessions shorter than 30 seconds do not count`() {
        val stats = build(
            StatsPeriod.WEEK,
            sessions = listOf(session("2026-11-10T07:00:00", seconds = 29), session("2026-11-10T08:00:00", seconds = 30)),
        )
        assertEquals(Duration.ofSeconds(30), stats.calmTotal)
    }

    @Test
    fun `a session that starts before midnight belongs to the day it started`() {
        val stats = build(StatsPeriod.WEEK, sessions = listOf(session("2026-11-09T23:50:00", minutes = 20)))

        assertEquals(Duration.ofMinutes(20), stats.bars[0].calm) // lunes
        assertEquals(Duration.ZERO, stats.bars[1].calm) // martes
    }

    @Test
    fun `the week is compared with the same days of the previous week`() {
        val stats = build(
            StatsPeriod.WEEK,
            sessions = listOf(
                // Semana pasada: lunes 2 y martes 3 entran en la comparación; el jueves 5 no.
                session("2026-11-02T08:00:00", minutes = 30),
                session("2026-11-03T08:00:00", minutes = 30),
                session("2026-11-05T08:00:00", minutes = 120),
                // Esta semana: lunes 9 y martes 10.
                session("2026-11-09T08:00:00", minutes = 45),
                session("2026-11-10T08:00:00", minutes = 45),
            ),
        )

        assertEquals(50, stats.calmChangePercent) // 90 min frente a 60 min
    }

    @Test
    fun `there is no comparison when the previous period or the current one is zero`() {
        val onlyCurrent = build(StatsPeriod.WEEK, sessions = listOf(session("2026-11-10T08:00:00", minutes = 10)))
        val onlyPrevious = build(StatsPeriod.WEEK, sessions = listOf(session("2026-11-03T08:00:00", minutes = 10)))

        assertNull(onlyCurrent.calmChangePercent)
        assertNull(onlyPrevious.calmChangePercent)
    }

    @Test
    fun `the change is rounded to the nearest whole percent`() {
        assertEquals(-67, StatsCalculator.changePercent(Duration.ofMinutes(1), Duration.ofMinutes(3)))
        assertEquals(33, StatsCalculator.changePercent(Duration.ofMinutes(4), Duration.ofMinutes(3)))
        assertEquals(0, StatsCalculator.changePercent(Duration.ofMinutes(5), Duration.ofMinutes(5)))
    }

    // --- Mes ---

    @Test
    fun `the month bars are Monday-to-Sunday weeks cut at the edges of the month`() {
        // Noviembre de 2026 empieza en domingo y termina en lunes.
        val stats = build(StatsPeriod.MONTH)

        assertEquals(DateSpan(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 1)), stats.span)
        assertEquals(
            listOf(1 to 1, 2 to 8, 9 to 15, 16 to 22, 23 to 29, 30 to 30),
            stats.bars.map { it.span.start.dayOfMonth to it.span.lastDay.dayOfMonth },
        )
    }

    @Test
    fun `the month is compared with the same days of the previous month, up to its last day`() {
        // 31 de marzo de 2027 frente al 1–28 de febrero (febrero de 2027 tiene 28 días).
        val stats = build(
            StatsPeriod.MONTH,
            today = LocalDate.of(2027, 3, 31),
            sessions = listOf(
                session("2027-02-28T08:00:00", minutes = 20),
                session("2027-03-31T08:00:00", minutes = 30),
            ),
        )

        assertEquals(Duration.ofMinutes(30), stats.calmTotal)
        assertEquals(50, stats.calmChangePercent)
    }

    // --- Total ---

    @Test
    fun `total starts at the first record, has no comparison and draws the last 12 months`() {
        val stats = build(
            StatsPeriod.TOTAL,
            sessions = listOf(
                session("2025-06-15T08:00:00", minutes = 10),
                session("2026-11-10T08:00:00", minutes = 5),
            ),
        )

        assertEquals(LocalDate.of(2025, 6, 15), stats.span.start)
        assertEquals(Duration.ofMinutes(15), stats.calmTotal) // la suma incluye todo
        assertNull(stats.calmChangePercent)
        assertEquals(StatsCalculator.MAX_TOTAL_BARS, stats.bars.size)
        assertEquals(LocalDate.of(2025, 12, 1), stats.bars.first().span.start)
        assertEquals(Duration.ofMinutes(5), stats.bars.last().calm)
    }

    @Test
    fun `total also starts at the first mood if it is older than the first session`() {
        val stats = build(
            StatsPeriod.TOTAL,
            sessions = listOf(session("2026-11-10T08:00:00", minutes = 5)),
            moods = listOf(mood("2026-09-01T08:00:00", Mood.CALM)),
        )
        assertEquals(LocalDate.of(2026, 9, 1), stats.span.start)
        assertEquals(3, stats.bars.size) // septiembre, octubre, noviembre
    }

    @Test
    fun `without records the period is empty`() {
        val stats = build(StatsPeriod.TOTAL)

        assertTrue(stats.isEmpty)
        assertEquals(DateSpan(tuesday, tuesday.plusDays(1)), stats.span)
        assertNull(stats.moods)
    }

    // --- Estados de ánimo ---

    @Test
    fun `mood percentages always add up to 100`() {
        val stats = build(
            StatsPeriod.WEEK,
            moods = listOf(
                mood("2026-11-09T08:00:00", Mood.CALM),
                mood("2026-11-09T09:00:00", Mood.ANXIOUS),
                mood("2026-11-10T08:00:00", Mood.SAD),
            ),
        )
        val shares = stats.moods!!.shares

        assertEquals(100, shares.sumOf { it.percent })
        // Empate de 33,3 % cada uno: el punto que falta va al elegido más recientemente.
        assertEquals(MoodShare(Mood.SAD, count = 1, percent = 34), shares.first())
    }

    @Test
    fun `the most frequent mood comes first and its share is rounded`() {
        val stats = build(
            StatsPeriod.WEEK,
            moods = listOf(
                mood("2026-11-09T08:00:00", Mood.CALM),
                mood("2026-11-09T20:00:00", Mood.CALM),
                mood("2026-11-10T08:00:00", Mood.STRESSED),
            ),
        )
        val moods = stats.moods!!

        assertEquals(Mood.CALM, moods.mostFrequent.mood)
        assertEquals(listOf(67, 33), moods.shares.map { it.percent })
        assertEquals(3, moods.total)
    }

    @Test
    fun `when two moods tie, the most recent one is the most frequent`() {
        val stats = build(
            StatsPeriod.WEEK,
            moods = listOf(
                mood("2026-11-09T08:00:00", Mood.HAPPY),
                mood("2026-11-09T09:00:00", Mood.CALM),
                mood("2026-11-10T08:00:00", Mood.HAPPY),
                mood("2026-11-10T09:00:00", Mood.CALM),
            ),
        )
        assertEquals(Mood.CALM, stats.moods!!.mostFrequent.mood)
    }

    @Test
    fun `moods outside the period are not counted`() {
        val stats = build(
            StatsPeriod.WEEK,
            moods = listOf(mood("2026-11-08T08:00:00", Mood.SAD), mood("2026-11-10T08:00:00", Mood.HAPPY)),
        )
        assertEquals(listOf(MoodShare(Mood.HAPPY, 1, 100)), stats.moods!!.shares)
    }
}

package com.panbu.app.domain.model

import java.time.Duration
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Reglas de docs/05-modelo-de-datos.md: sesiones que cuentan, tiempo de calma y avance de metas. */
class GoalTrackingTest {

    private val base = Instant.parse("2026-11-10T15:00:00Z")

    private fun session(type: CalmActivityType, seconds: Long, startOffsetMin: Long = 0) = CalmSession(
        type = type,
        source = DataSource.MANUAL,
        startedAt = base.plusSeconds(startOffsetMin * 60),
        endedAt = base.plusSeconds(startOffsetMin * 60 + seconds),
    )

    private val sessions = listOf(
        session(CalmActivityType.BREATHING, seconds = 60),
        session(CalmActivityType.BREATHING, seconds = 20, startOffsetMin = 5), // muy corta: no cuenta
        session(CalmActivityType.MOMENT_WITH_PANBU, seconds = 600, startOffsetMin = 10),
        session(CalmActivityType.MINDFULNESS, seconds = 30, startOffsetMin = 30), // justo 30 s: cuenta
    )

    @Test
    fun `sessions shorter than 30 seconds do not count`() {
        assertEquals(3, sessions.counted().size)
    }

    @Test
    fun `total calm time only adds counted sessions`() {
        assertEquals(Duration.ofSeconds(60 + 600 + 30), sessions.totalDuration())
    }

    @Test
    fun `calm moments count every counted session`() {
        val goal = DefaultGoals.all.first { it.type == GoalType.CALM_MOMENTS }
        assertEquals(3, goal.progressFrom(sessions)?.current)
    }

    @Test
    fun `mindfulness counts only breathing and mindfulness sessions`() {
        val goal = DefaultGoals.all.first { it.type == GoalType.MINDFULNESS }
        assertEquals(2, goal.progressFrom(sessions)?.current)
    }

    @Test
    fun `goals without a data source show no progress instead of an invented number`() {
        val sleep = Goal(type = GoalType.SLEEP, title = "", target = 480, unit = GoalUnit.MINUTES, period = GoalPeriod.DAILY)
        assertNull(sleep.progressFrom(sessions))
    }

    @Test
    fun `no sessions means zero progress, not missing data`() {
        val goal = DefaultGoals.all.first()
        assertEquals(0, goal.progressFrom(emptyList())?.current)
    }
}

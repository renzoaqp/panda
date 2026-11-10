package com.panbu.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Reglas de "Progreso de meta" y "Meta cumplida" de docs/05-modelo-de-datos.md. */
class GoalProgressTest {

    private fun goal(type: GoalType, target: Int, unit: GoalUnit = GoalUnit.MINUTES) =
        Goal(type = type, title = "Meta", target = target, unit = unit, period = GoalPeriod.DAILY)

    @Test
    fun `percent is floored like in the mockup`() {
        // Dormir mejor: 7 h 10 min de 8 h -> 89 %
        assertEquals(89, GoalProgress(goal(GoalType.SLEEP, target = 480), current = 430).percent)
        // Respiración: 4 de 5 veces -> 80 %
        assertEquals(80, GoalProgress(goal(GoalType.MINDFULNESS, 5, GoalUnit.TIMES), current = 4).percent)
    }

    @Test
    fun `percent has no floating point rounding errors`() {
        // 0.7f * 100 en punto flotante daría 69.99…; con enteros es 70.
        assertEquals(70, GoalProgress(goal(GoalType.CUSTOM, target = 10), current = 7).percent)
    }

    @Test
    fun `progress is clamped between zero and one`() {
        val over = GoalProgress(goal(GoalType.CALM_MOMENTS, 3, GoalUnit.TIMES), current = 5)
        assertEquals(1f, over.fraction, 0.0001f)
        assertEquals(100, over.percent)
        assertTrue(over.isAchieved)

        val negative = GoalProgress(goal(GoalType.CUSTOM, target = 10), current = -3)
        assertEquals(0f, negative.fraction, 0.0001f)
        assertEquals(0, negative.percent)
    }

    @Test
    fun `limit goal is achieved while usage stays within the limit`() {
        // Menos celular en la noche: 1 h 12 min de un límite de 2 h -> 60 %, dentro del límite
        val limit = goal(GoalType.NIGHT_SCREEN_LIMIT, target = 120)
        assertEquals(60, GoalProgress(limit, current = 72).percent)
        assertTrue(GoalProgress(limit, current = 72).isAchieved)
        assertTrue(GoalProgress(limit, current = 120).isAchieved)
        assertFalse(GoalProgress(limit, current = 130).isAchieved)
    }

    @Test
    fun `zero target never divides by zero`() {
        val progress = GoalProgress(goal(GoalType.CUSTOM, target = 0), current = 4)
        assertEquals(0f, progress.fraction, 0.0001f)
        assertEquals(0, progress.percent)
        assertFalse(progress.isAchieved)
    }
}

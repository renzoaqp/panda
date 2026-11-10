package com.panbu.app.ui.components.charts

import java.time.Duration
import org.junit.Assert.assertEquals
import org.junit.Test

/** El eje del gráfico de barras elige un paso redondo y como máximo 4 divisiones. */
class MinutesAxisTest {

    private fun axisFor(minutes: Long, seconds: Long = 0) =
        MinutesAxis.forMaximum(Duration.ofMinutes(minutes).plusSeconds(seconds))

    @Test
    fun `three and a half hours give an axis from 0 to 4 h in steps of 1 h, like the mockup`() {
        val axis = axisFor(210)
        assertEquals(60L, axis.stepMinutes)
        assertEquals(listOf(0L, 60, 120, 180, 240), axis.ticks)
    }

    @Test
    fun `small amounts use steps of minutes`() {
        assertEquals(MinutesAxis(stepMinutes = 5, steps = 3), axisFor(15))
        assertEquals(MinutesAxis(stepMinutes = 2, steps = 4), axisFor(7))
        assertEquals(MinutesAxis(stepMinutes = 1, steps = 1), axisFor(1))
    }

    @Test
    fun `less than a minute or nothing still gives an axis of 1 min`() {
        assertEquals(MinutesAxis(stepMinutes = 1, steps = 1), axisFor(0, seconds = 40))
        assertEquals(MinutesAxis(stepMinutes = 1, steps = 1), axisFor(0))
    }

    @Test
    fun `seconds round the maximum up so the tallest bar always fits`() {
        val axis = axisFor(20, seconds = 1) // 20 min 1 s → necesita 21 min
        assertEquals(30L, axis.maxMinutes)
        assertEquals(1f, axisFor(4).fractionOf(Duration.ofMinutes(4)))
    }

    @Test
    fun `very large amounts use whole hours`() {
        val axis = axisFor(100 * 60) // 100 h en un mes
        assertEquals(0L, axis.stepMinutes % 60)
        assertEquals(true, axis.steps <= MinutesAxis.MAX_STEPS)
        assertEquals(true, axis.maxMinutes >= 100 * 60)
    }
}

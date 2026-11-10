package com.panbu.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BreathingPatternTest {

    @Test
    fun `starts inhaling with the full inhale time left`() {
        val step = BreathingPattern.Gentle.stepAt(0)
        assertEquals(BreathingPhase.INHALE, step.phase)
        assertEquals(4, step.secondsLeft)
        assertEquals(0f, step.phaseProgress, 0.0001f)
    }

    @Test
    fun `phases with zero seconds are skipped`() {
        // Suave 4-0-6: después de inhalar viene exhalar directamente (no hay "sostén").
        val step = BreathingPattern.Gentle.stepAt(4_000)
        assertEquals(BreathingPhase.EXHALE, step.phase)
        assertEquals(6, step.secondsLeft)
    }

    @Test
    fun `seconds left are rounded up`() {
        val step = BreathingPattern.Gentle.stepAt(1_500)
        assertEquals(BreathingPhase.INHALE, step.phase)
        assertEquals(3, step.secondsLeft) // quedan 2,5 s → se muestra 3
    }

    @Test
    fun `cycles repeat`() {
        // Suave dura 10 s: a los 10 s vuelve a inhalar.
        assertEquals(BreathingPhase.INHALE, BreathingPattern.Gentle.stepAt(10_000).phase)
        assertEquals(BreathingPhase.EXHALE, BreathingPattern.Gentle.stepAt(25_000).phase)
    }

    @Test
    fun `box breathing goes through the four phases`() {
        val box = BreathingPattern.Box
        assertEquals(BreathingPhase.INHALE, box.stepAt(0).phase)
        assertEquals(BreathingPhase.HOLD, box.stepAt(4_000).phase)
        assertEquals(BreathingPhase.EXHALE, box.stepAt(8_000).phase)
        assertEquals(BreathingPhase.HOLD_EMPTY, box.stepAt(12_000).phase)
        assertEquals(BreathingPhase.INHALE, box.stepAt(16_000).phase)
    }

    @Test
    fun `4-7-8 exhales after holding seven seconds`() {
        val step = BreathingPattern.Relax478.stepAt(11_000)
        assertEquals(BreathingPhase.EXHALE, step.phase)
        assertEquals(8, step.secondsLeft)
    }

    @Test
    fun `negative time is treated as the start`() {
        assertEquals(BreathingPhase.INHALE, BreathingPattern.Gentle.stepAt(-500).phase)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a pattern without duration is rejected`() {
        BreathingPattern(0, 0, 0)
    }
}

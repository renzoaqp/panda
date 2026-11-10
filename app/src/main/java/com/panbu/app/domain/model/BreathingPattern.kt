package com.panbu.app.domain.model

/**
 * Patrón de respiración en segundos. Lo usan la animación de "Respira con Panbu" (Fase 1)
 * y, en la Fase 5, el peluche (vibración o luz).
 */
data class BreathingPattern(
    val inhaleSeconds: Int,
    val holdSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int = 0,
) {
    val cycleSeconds: Int get() = inhaleSeconds + holdSeconds + exhaleSeconds + holdAfterExhaleSeconds

    companion object {
        /** Respiración en caja 4-4-4-4. */
        val Box = BreathingPattern(4, 4, 4, 4)

        /** Respiración 4-7-8, para relajarse antes de dormir. */
        val Relax478 = BreathingPattern(4, 7, 8)

        /** Suave 4-6, la del botón "Necesito un momento de calma". */
        val Gentle = BreathingPattern(4, 0, 6)
    }
}

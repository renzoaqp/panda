package com.panbu.app.domain.model

import kotlin.math.ceil

/**
 * Patrón de respiración en segundos. Lo usan la animación de "Respira con Panbu" y, en la Fase 5,
 * el peluche (vibración o luz).
 */
data class BreathingPattern(
    val inhaleSeconds: Int,
    val holdSeconds: Int,
    val exhaleSeconds: Int,
    val holdAfterExhaleSeconds: Int = 0,
) {
    init {
        require(cycleSeconds > 0) { "Un ciclo de respiración debe durar más de 0 segundos" }
    }

    val cycleSeconds: Int get() = inhaleSeconds + holdSeconds + exhaleSeconds + holdAfterExhaleSeconds

    /** Qué fase toca en el instante [elapsedMillis] desde que empezó el ejercicio (los ciclos se repiten). */
    fun stepAt(elapsedMillis: Long): BreathingStep {
        val phases = listOf(
            BreathingPhase.INHALE to inhaleSeconds,
            BreathingPhase.HOLD to holdSeconds,
            BreathingPhase.EXHALE to exhaleSeconds,
            BreathingPhase.HOLD_EMPTY to holdAfterExhaleSeconds,
        )
        var t = elapsedMillis.coerceAtLeast(0) % (cycleSeconds * 1000L)
        for ((phase, seconds) in phases) {
            val phaseMillis = seconds * 1000L
            if (phaseMillis == 0L) continue
            if (t < phaseMillis) {
                return BreathingStep(
                    phase = phase,
                    phaseSeconds = seconds,
                    phaseProgress = t.toFloat() / phaseMillis,
                    secondsLeft = ceil((phaseMillis - t) / 1000.0).toInt(),
                )
            }
            t -= phaseMillis
        }
        error("No se encontró la fase de respiración")
    }

    companion object {
        /** Suave 4-6: la del botón "Necesito un momento de calma". */
        val Gentle = BreathingPattern(4, 0, 6)

        /** Respiración en caja 4-4-4-4. */
        val Box = BreathingPattern(4, 4, 4, 4)

        /** Respiración 4-7-8, para relajarse antes de dormir. */
        val Relax478 = BreathingPattern(4, 7, 8)
    }
}

enum class BreathingPhase { INHALE, HOLD, EXHALE, HOLD_EMPTY }

/**
 * @param phaseSeconds duración total de la fase actual.
 * @param phaseProgress de 0 a 1 dentro de la fase.
 * @param secondsLeft segundos que faltan para cambiar de fase (redondeado hacia arriba).
 */
data class BreathingStep(
    val phase: BreathingPhase,
    val phaseSeconds: Int,
    val phaseProgress: Float,
    val secondsLeft: Int,
)

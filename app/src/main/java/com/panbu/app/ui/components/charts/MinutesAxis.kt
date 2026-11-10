package com.panbu.app.ui.components.charts

import java.time.Duration
import kotlin.math.ceil

/**
 * Eje vertical de un gráfico de tiempo: va de 0 a [maxMinutes] en [steps] divisiones de [stepMinutes].
 * El paso es un número "redondo" (5 min, 15 min, 1 h…) y hay como máximo [MAX_STEPS] divisiones,
 * para que las etiquetas se lean bien (como "0 h · 1 h · 2 h · 3 h · 4 h" en el mockup).
 */
data class MinutesAxis(val stepMinutes: Long, val steps: Int) {

    val maxMinutes: Long get() = stepMinutes * steps

    /** Valores de las líneas guía, de abajo hacia arriba, empezando en 0. */
    val ticks: List<Long> get() = (0..steps).map { it * stepMinutes }

    /** Altura de una barra en fracción del eje (0–1). */
    fun fractionOf(value: Duration): Float =
        (value.seconds.toFloat() / (maxMinutes * 60f)).coerceIn(0f, 1f)

    companion object {
        const val MAX_STEPS = 4

        private val NICE_STEPS = listOf(1L, 2, 5, 10, 15, 30, 60, 120, 180, 240, 360, 480, 720, 1440)

        /** El eje más pequeño que contiene [maximum]. Con 0 (o menos de un minuto) el eje llega a 1 min. */
        fun forMaximum(maximum: Duration): MinutesAxis {
            val maxMinutes = ceil(maximum.seconds.coerceAtLeast(0) / 60.0).toLong().coerceAtLeast(1)
            val step = NICE_STEPS.firstOrNull { stepsFor(maxMinutes, it) <= MAX_STEPS }
                // Más de 4 días en una barra: pasos de horas enteras.
                ?: (ceil(maxMinutes / (MAX_STEPS * 60.0)).toLong() * 60)
            return MinutesAxis(stepMinutes = step, steps = stepsFor(maxMinutes, step).coerceAtLeast(1))
        }

        private fun stepsFor(maxMinutes: Long, step: Long): Int = ceil(maxMinutes.toDouble() / step).toInt()
    }
}

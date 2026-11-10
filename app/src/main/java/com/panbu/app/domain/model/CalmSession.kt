package com.panbu.app.domain.model

import java.time.Duration
import java.time.Instant

/** Tipo de actividad de calma. Se guarda por `name`: no renombrar valores publicados. */
enum class CalmActivityType { BREATHING, MINDFULNESS, SOUNDS, MOMENT_WITH_PANBU, REST_ROUTINE }

/** De dónde viene un dato: registrado en la app o enviado por el peluche (Fase 5). */
enum class DataSource { MANUAL, DEVICE }

/**
 * Tiempo dedicado a una actividad de calma. La suma de las sesiones es el
 * "Tiempo contigo" / "Tiempo de calma" (docs/05-modelo-de-datos.md).
 */
data class CalmSession(
    val id: Long = 0,
    val type: CalmActivityType,
    val source: DataSource,
    val startedAt: Instant,
    val endedAt: Instant,
) {
    val duration: Duration get() = Duration.between(startedAt, endedAt)

    companion object {
        /** Las sesiones más cortas no cuentan en las estadísticas. */
        val MIN_COUNTED_DURATION: Duration = Duration.ofSeconds(30)
    }
}

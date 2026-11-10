package com.panbu.app.domain.model

import java.time.Instant

/**
 * Estados de ánimo que la persona puede elegir (guía del cliente, sección 2).
 *
 * Se guardan en la base de datos por su `name`: NUNCA renombrar un valor ya publicado.
 */
enum class Mood { CALM, ANXIOUS, SAD, STRESSED, HAPPY }

/** Un registro de "¿Cómo te sientes?". */
data class MoodEntry(
    val id: Long = 0,
    val mood: Mood,
    val note: String? = null,
    val createdAt: Instant,
)

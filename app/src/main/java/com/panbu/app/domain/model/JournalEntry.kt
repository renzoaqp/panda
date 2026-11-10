package com.panbu.app.domain.model

import java.time.Instant

/**
 * Una entrada de "Mi diario emocional" (docs/02-producto.md §4).
 *
 * Es privada: el texto se guarda cifrado y **nunca se analiza** (guardrail 4). El estado de ánimo del diario
 * es opcional y no se suma a Estadísticas (ahí solo cuenta lo que se elige en Inicio).
 */
data class JournalEntry(
    val id: Long = 0,
    val createdAt: Instant,
    val updatedAt: Instant,
    val mood: Mood? = null,
    /**
     * El texto ya descifrado, o null si no se pudo descifrar (por ejemplo, si Android borró la clave del
     * Keystore). La entrada se muestra como "No se pudo leer" y se puede borrar.
     */
    val text: String?,
) {
    companion object {
        /** Largo máximo de una entrada: unas 3 páginas. */
        const val MAX_LENGTH = 10_000
    }
}

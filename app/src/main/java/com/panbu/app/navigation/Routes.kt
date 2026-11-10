package com.panbu.app.navigation

import kotlinx.serialization.Serializable

// Rutas tipadas de Navigation Compose. Una ruta nueva se registra en PanbuNavHost.kt.

@Serializable
data object HomeRoute

@Serializable
data object StatsRoute

@Serializable
data object GoalsRoute

@Serializable
data object CalmRoute

@Serializable
data object SettingsRoute

/** Pantalla secundaria (sin barra inferior). Solo se entra desde la pestaña Panbu. */
@Serializable
data object ShopRoute

/**
 * Respira con Panbu (pantalla completa). `quickStart = true` viene del botón "Necesito un momento de calma":
 * empieza enseguida 1 minuto de respiración suave, sin pasos previos.
 */
@Serializable
data class BreathingRoute(val quickStart: Boolean = false)

/** Un momento con Panbu: temporizador del "Tiempo contigo" (pantalla completa). */
@Serializable
data object MomentRoute

/** Mi diario emocional: lista de entradas (pantalla secundaria, se entra desde la pestaña Panbu). */
@Serializable
data object JournalRoute

/** Escribir o editar una entrada del diario. `entryId = 0` es una entrada nueva. */
@Serializable
data class JournalEditorRoute(val entryId: Long = 0)

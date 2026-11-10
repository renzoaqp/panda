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

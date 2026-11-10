package com.panbu.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.panbu.app.R
import kotlin.reflect.KClass

/** Las 5 pestañas de la barra inferior (guía del cliente, sección 6), en orden. */
enum class TopLevelDestination(
    val routeClass: KClass<*>,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
) {
    HOME(HomeRoute::class, R.string.nav_home, R.drawable.ic_nav_home),
    STATS(StatsRoute::class, R.string.nav_stats, R.drawable.ic_nav_stats),
    GOALS(GoalsRoute::class, R.string.nav_goals, R.drawable.ic_nav_goals),
    PANBU(CalmRoute::class, R.string.nav_panbu, R.drawable.ic_nav_panbu),
    SETTINGS(SettingsRoute::class, R.string.nav_settings, R.drawable.ic_nav_settings),
}

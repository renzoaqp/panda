package com.panbu.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.feature.onboarding.OnboardingScreen
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlin.reflect.KClass

/**
 * Raíz de la interfaz. La primera vez (o después de "Borrar todos mis datos") muestra la bienvenida; después,
 * la barra inferior y la pestaña actual. Mientras se leen las preferencias (un instante) solo se ve el fondo.
 */
@Composable
fun PanbuAppRoot(viewModel: AppViewModel = hiltViewModel()) {
    val start by viewModel.start.collectAsStateWithLifecycle()
    when (start) {
        AppStart.LOADING -> Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        )
        AppStart.ONBOARDING -> OnboardingScreen()
        AppStart.MAIN -> MainScaffold()
    }
}

/** Barra inferior + contenido de la pestaña actual. */
@Composable
private fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Pantallas secundarias o de calma a pantalla completa: sin barra inferior.
    val showBottomBar = currentDestination?.let { destination ->
        FullScreenRoutes.none { destination.hasRoute(it) }
    } ?: true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                PanbuBottomBar(
                    isSelected = { destination -> currentDestination.isInHierarchy(destination.routeClass) },
                    onNavigate = navController::navigateToTopLevel,
                )
            }
        },
    ) { innerPadding ->
        PanbuNavHost(
            navController = navController,
            onOpenTopLevel = navController::navigateToTopLevel,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

private val FullScreenRoutes: List<KClass<*>> = listOf(ShopRoute::class, BreathingRoute::class, MomentRoute::class)

private fun NavDestination?.isInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false

/** Cambia de pestaña conservando el estado de cada una (scroll, etc.). */
private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    val options = navOptions {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
    when (destination) {
        TopLevelDestination.HOME -> navigate(HomeRoute, options)
        TopLevelDestination.STATS -> navigate(StatsRoute, options)
        TopLevelDestination.GOALS -> navigate(GoalsRoute, options)
        TopLevelDestination.PANBU -> navigate(CalmRoute, options)
        TopLevelDestination.SETTINGS -> navigate(SettingsRoute, options)
    }
}

package com.panbu.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.panbu.app.feature.calm.CalmSpaceScreen
import com.panbu.app.feature.goals.GoalsScreen
import com.panbu.app.feature.home.HomeScreen
import com.panbu.app.feature.settings.SettingsScreen
import com.panbu.app.feature.shop.ShopScreen
import com.panbu.app.feature.stats.StatsScreen

@Composable
fun PanbuNavHost(
    navController: NavHostController,
    onOpenTopLevel: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        composable<HomeRoute> {
            HomeScreen(onOpenSettings = { onOpenTopLevel(TopLevelDestination.SETTINGS) })
        }
        composable<StatsRoute> { StatsScreen() }
        composable<GoalsRoute> { GoalsScreen() }
        composable<CalmRoute> {
            CalmSpaceScreen(onOpenShop = { navController.navigate(ShopRoute) })
        }
        composable<SettingsRoute> { SettingsScreen() }
        composable<ShopRoute> {
            ShopScreen(onBack = { navController.popBackStack() })
        }
    }
}

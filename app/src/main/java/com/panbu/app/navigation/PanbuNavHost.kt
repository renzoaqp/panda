package com.panbu.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.panbu.app.feature.breathing.BreathingScreen
import com.panbu.app.feature.calm.CalmSpaceScreen
import com.panbu.app.feature.goals.GoalsScreen
import com.panbu.app.feature.home.HomeScreen
import com.panbu.app.feature.journal.JournalEditorScreen
import com.panbu.app.feature.journal.JournalScreen
import com.panbu.app.feature.moment.MomentScreen
import com.panbu.app.feature.settings.SettingsScreen
import com.panbu.app.feature.shop.ShopScreen
import com.panbu.app.feature.stats.StatsScreen

@Composable
fun PanbuNavHost(
    navController: NavHostController,
    onOpenTopLevel: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val back: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onOpenSettings = { onOpenTopLevel(TopLevelDestination.SETTINGS) },
                onOpenBreathing = { navController.navigate(BreathingRoute()) },
                onOpenMoment = { navController.navigate(MomentRoute) },
            )
        }
        composable<StatsRoute> { StatsScreen(onOpenBreathing = { navController.navigate(BreathingRoute()) }) }
        composable<GoalsRoute> { GoalsScreen() }
        composable<CalmRoute> {
            CalmSpaceScreen(
                onNeedCalm = { navController.navigate(BreathingRoute(quickStart = true)) },
                onOpenBreathing = { navController.navigate(BreathingRoute()) },
                onOpenJournal = { navController.navigate(JournalRoute) },
                onOpenShop = { navController.navigate(ShopRoute) },
            )
        }
        composable<SettingsRoute> { SettingsScreen() }
        composable<ShopRoute> { ShopScreen(onBack = back) }
        composable<BreathingRoute> { BreathingScreen(onClose = back) }
        composable<MomentRoute> { MomentScreen(onClose = back) }
        composable<JournalRoute> {
            JournalScreen(
                onBack = back,
                onWrite = { navController.navigate(JournalEditorRoute()) },
                onOpenEntry = { id -> navController.navigate(JournalEditorRoute(entryId = id)) },
            )
        }
        composable<JournalEditorRoute> { JournalEditorScreen(onClose = back) }
    }
}

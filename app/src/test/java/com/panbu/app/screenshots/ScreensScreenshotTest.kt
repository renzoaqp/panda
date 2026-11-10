package com.panbu.app.screenshots

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import com.panbu.app.domain.model.Mood
import com.panbu.app.feature.calm.CalmSpaceScreen
import com.panbu.app.feature.goals.GoalsScreen
import com.panbu.app.feature.home.HomeContent
import com.panbu.app.feature.home.HomeUiState
import com.panbu.app.feature.settings.SettingsScreen
import com.panbu.app.feature.shop.ShopScreen
import com.panbu.app.feature.stats.StatsScreen
import com.panbu.app.navigation.PanbuBottomBar
import com.panbu.app.navigation.TopLevelDestination
import com.panbu.app.ui.theme.PanbuTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Capturas de cada pantalla, para revisarlas sin Android Studio ni celular.
 *
 *   ./gradlew recordRoborazziDebug   → PNG en app/build/outputs/roborazzi/
 *
 * Usan contenido sin estado (sin Hilt ni base de datos), por eso la app de prueba es una Application simple.
 */
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7, application = Application::class)
class ScreensScreenshotTest {

    private fun capture(name: String, tab: TopLevelDestination?, content: @Composable () -> Unit) {
        captureRoboImage("build/outputs/roborazzi/$name.png") {
            PanbuTheme {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (tab != null) PanbuBottomBar(isSelected = { it == tab }, onNavigate = {})
                    },
                ) { padding ->
                    Box(Modifier.padding(padding)) { content() }
                }
            }
        }
    }

    @Test
    fun home() = capture("01_inicio", TopLevelDestination.HOME) {
        HomeContent(uiState = HomeUiState(todayMood = Mood.CALM), onMoodSelected = {}, onOpenSettings = {})
    }

    @Test
    fun homeEmpty() = capture("01b_inicio_sin_registro", TopLevelDestination.HOME) {
        HomeContent(uiState = HomeUiState(), onMoodSelected = {}, onOpenSettings = {})
    }

    @Test
    fun stats() = capture("02_estadisticas", TopLevelDestination.STATS) { StatsScreen() }

    @Test
    fun goals() = capture("03_metas", TopLevelDestination.GOALS) { GoalsScreen() }

    @Test
    fun calm() = capture("04_panbu", TopLevelDestination.PANBU) { CalmSpaceScreen(onOpenShop = {}) }

    @Test
    fun settings() = capture("05_ajustes", TopLevelDestination.SETTINGS) { SettingsScreen() }

    @Test
    fun shop() = capture("06_tienda", tab = null) { ShopScreen(onBack = {}) }
}

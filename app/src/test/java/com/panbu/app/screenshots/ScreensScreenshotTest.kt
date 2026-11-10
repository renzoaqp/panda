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
import com.panbu.app.domain.model.DefaultGoals
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.model.GoalProgress
import com.panbu.app.domain.model.Mood
import com.panbu.app.feature.breathing.BreathingContent
import com.panbu.app.feature.breathing.BreathingPreset
import com.panbu.app.feature.breathing.BreathingStage
import com.panbu.app.feature.breathing.BreathingUiState
import com.panbu.app.feature.calm.CalmSpaceScreen
import com.panbu.app.feature.goals.GoalsContent
import com.panbu.app.feature.goals.GoalsUiState
import com.panbu.app.feature.home.HomeContent
import com.panbu.app.feature.home.HomeUiState
import com.panbu.app.feature.moment.MomentContent
import com.panbu.app.feature.moment.MomentStage
import com.panbu.app.feature.moment.MomentUiState
import com.panbu.app.feature.settings.SettingsScreen
import com.panbu.app.feature.shop.ShopScreen
import com.panbu.app.feature.stats.StatsScreen
import com.panbu.app.navigation.PanbuBottomBar
import com.panbu.app.navigation.TopLevelDestination
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Duration
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Capturas de cada pantalla, para revisarlas sin Android Studio ni celular.
 *
 *   ./scripts/gradlew.sh recordRoborazziDebug   → PNG en app/build/outputs/roborazzi/
 *
 * Usan contenido sin estado (sin Hilt ni base de datos), por eso la app de prueba es una Application simple.
 * Los datos de estas capturas son de ejemplo y viven solo en los tests (guardrail 8).
 */
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel7, application = Application::class)
class ScreensScreenshotTest {

    private val exampleGoals = DefaultGoals.all.mapIndexed { i, goal ->
        GoalItem(goal, GoalProgress(goal, current = if (i == 0) 3 else 1))
    }

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
        HomeContent(
            uiState = HomeUiState(
                todayMood = Mood.CALM,
                calmToday = Duration.ofMinutes(135),
                calmYesterday = Duration.ofMinutes(90),
                goals = exampleGoals,
            ),
            onMoodSelected = {},
            onOpenSettings = {},
        )
    }

    @Test
    fun homeEmpty() = capture("01b_inicio_primer_dia", TopLevelDestination.HOME) {
        HomeContent(
            uiState = HomeUiState(goals = DefaultGoals.all.map { GoalItem(it, GoalProgress(it, current = 0)) }),
            onMoodSelected = {},
            onOpenSettings = {},
        )
    }

    @Test
    fun stats() = capture("02_estadisticas", TopLevelDestination.STATS) { StatsScreen() }

    @Test
    fun goals() = capture("03_metas", TopLevelDestination.GOALS) { GoalsContent(GoalsUiState(goals = exampleGoals)) }

    @Test
    fun calm() = capture("04_panbu", TopLevelDestination.PANBU) {
        CalmSpaceScreen(onNeedCalm = {}, onOpenBreathing = {}, onOpenShop = {})
    }

    @Test
    fun breathingSetup() = capture("04a_respira_inicio", tab = null) {
        BreathingContent(BreathingUiState(), {}, {}, {}, {}, {}, {})
    }

    @Test
    fun breathingRunning() = capture("04b_respira_en_curso", tab = null) {
        BreathingContent(
            BreathingUiState(
                stage = BreathingStage.RUNNING,
                minutes = 3,
                elapsedMillis = 62_000,
                step = BreathingPreset.GENTLE.pattern.stepAt(62_000),
            ),
            {}, {}, {}, {}, {}, {},
        )
    }

    @Test
    fun breathingFinished() = capture("04c_respira_fin", tab = null) {
        BreathingContent(
            BreathingUiState(stage = BreathingStage.FINISHED, finishedDuration = Duration.ofMinutes(3)),
            {}, {}, {}, {}, {}, {},
        )
    }

    @Test
    fun moment() = capture("04d_momento_en_curso", tab = null) {
        MomentContent(MomentUiState(stage = MomentStage.RUNNING, elapsedSeconds = 754), {}, {}, {})
    }

    @Test
    fun settings() = capture("05_ajustes", TopLevelDestination.SETTINGS) { SettingsScreen() }

    @Test
    fun shop() = capture("06_tienda", tab = null) { ShopScreen(onBack = {}) }
}

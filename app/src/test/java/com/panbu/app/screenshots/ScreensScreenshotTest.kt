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
import com.panbu.app.domain.model.CalmActivityType
import com.panbu.app.domain.model.CalmSession
import com.panbu.app.domain.model.DataSource
import com.panbu.app.domain.model.DefaultGoals
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.model.GoalProgress
import com.panbu.app.domain.model.JournalEntry
import com.panbu.app.domain.model.Mood
import com.panbu.app.domain.model.MoodEntry
import com.panbu.app.domain.model.StatsCalculator
import com.panbu.app.domain.model.StatsPeriod
import com.panbu.app.domain.model.UserPreferences
import com.panbu.app.feature.breathing.BreathingContent
import com.panbu.app.feature.breathing.BreathingPreset
import com.panbu.app.feature.breathing.BreathingStage
import com.panbu.app.feature.breathing.BreathingUiState
import com.panbu.app.feature.calm.CalmSpaceScreen
import com.panbu.app.feature.goals.GoalsContent
import com.panbu.app.feature.goals.GoalsUiState
import com.panbu.app.feature.home.HomeContent
import com.panbu.app.feature.journal.JournalContent
import com.panbu.app.feature.journal.JournalEditorContent
import com.panbu.app.feature.journal.JournalEditorUiState
import com.panbu.app.feature.journal.JournalUiState
import com.panbu.app.feature.home.HomeUiState
import com.panbu.app.feature.moment.MomentContent
import com.panbu.app.feature.moment.MomentStage
import com.panbu.app.feature.moment.MomentUiState
import com.panbu.app.feature.onboarding.OnboardingContent
import com.panbu.app.feature.onboarding.OnboardingStep
import com.panbu.app.feature.onboarding.OnboardingUiState
import com.panbu.app.feature.settings.SettingsContent
import com.panbu.app.feature.shop.ShopScreen
import com.panbu.app.feature.stats.StatsContent
import com.panbu.app.feature.stats.StatsUiState
import com.panbu.app.navigation.PanbuBottomBar
import com.panbu.app.navigation.TopLevelDestination
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
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
                userName = "Ana",
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

    // Estadísticas: se calculan con las mismas reglas de la app, a partir de registros de ejemplo.
    private val zone = ZoneId.of("America/Lima")
    private val sunday = LocalDate.of(2026, 11, 15)

    private fun session(day: LocalDate, minutes: Long) = day.atTime(20, 0).atZone(zone).toInstant().let {
        CalmSession(type = CalmActivityType.BREATHING, source = DataSource.MANUAL, startedAt = it, endedAt = it.plusSeconds(minutes * 60))
    }

    private val exampleSessions = buildList {
        // Semana pasada (para la comparación) y esta semana, de lunes a domingo.
        listOf(20L, 25, 30, 20, 35, 40, 30).forEachIndexed { i, min -> add(session(sunday.minusDays(13L - i), min)) }
        listOf(25L, 40, 38, 40, 45, 55, 70).forEachIndexed { i, min -> add(session(sunday.minusDays(6L - i), min)) }
        add(session(LocalDate.of(2026, 9, 20), 90))
        add(session(LocalDate.of(2026, 10, 5), 300))
    }

    private val exampleMoods = buildList {
        val moods = List(13) { Mood.CALM } + List(4) { Mood.ANXIOUS } + List(2) { Mood.STRESSED } + Mood.SAD
        moods.forEachIndexed { i, mood ->
            add(MoodEntry(mood = mood, createdAt = sunday.minusDays(i % 7L).atTime(9, i).atZone(zone).toInstant()))
        }
    }

    private fun stats(period: StatsPeriod, sessions: List<CalmSession> = exampleSessions, moods: List<MoodEntry> = exampleMoods) =
        StatsUiState(period, StatsCalculator.build(period, sunday, zone, sessions, moods))

    @Test
    fun stats() = capture("02_estadisticas", TopLevelDestination.STATS) {
        StatsContent(stats(StatsPeriod.WEEK), {}, {})
    }

    @Test
    fun statsMonth() = capture("02b_estadisticas_mes", TopLevelDestination.STATS) {
        StatsContent(stats(StatsPeriod.MONTH), {}, {})
    }

    @Test
    fun statsTotal() = capture("02c_estadisticas_total", TopLevelDestination.STATS) {
        StatsContent(stats(StatsPeriod.TOTAL), {}, {})
    }

    @Test
    fun statsEmpty() = capture("02d_estadisticas_vacia", TopLevelDestination.STATS) {
        StatsContent(stats(StatsPeriod.WEEK, sessions = emptyList(), moods = emptyList()), {}, {})
    }

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
        MomentContent(MomentUiState(stage = MomentStage.RUNNING, elapsedSeconds = 754), {}, {}, {}, animatePulse = false)
    }

    // Diario: textos de ejemplo escritos para la captura (en la app solo hay lo que escribe la persona).
    @Test
    fun journal() = capture("04e_diario", tab = null) {
        val entries = listOf(
            JournalEntry(1, sunday.atTime(21, 40).atZone(zone).toInstant(), sunday.atTime(21, 40).atZone(zone).toInstant(), Mood.CALM,
                "Hoy respiré con Panbu antes de dormir y me sentí más tranquila. Mañana quiero intentarlo otra vez."),
            JournalEntry(2, sunday.minusDays(2).atTime(8, 15).atZone(zone).toInstant(), sunday.minusDays(2).atTime(8, 15).atZone(zone).toInstant(), null,
                "Tres cosas buenas de hoy: el desayuno con mi familia, una caminata corta y terminar el trabajo a tiempo."),
        )
        JournalContent(JournalUiState(entries), {}, {}, {}, zone = zone)
    }

    @Test
    fun journalEmpty() = capture("04f_diario_vacio", tab = null) {
        JournalContent(JournalUiState(emptyList()), {}, {}, {}, zone = zone)
    }

    @Test
    fun journalEditor() = capture("04g_diario_escribir", tab = null) {
        JournalEditorContent(
            JournalEditorUiState(mood = Mood.CALM, text = "Hoy respiré con Panbu antes de dormir."),
            {}, {}, {}, {}, {}, zone = zone,
        )
    }

    @Test
    fun settings() = capture("05_ajustes", TopLevelDestination.SETTINGS) {
        SettingsContent(
            preferences = UserPreferences(userName = "Ana", notificationsEnabled = true),
            notificationsAllowed = true,
            permissionDenied = false,
            onNameChange = {}, onRemindersChange = {}, onRestScheduleChange = { _, _ -> },
            onReminderMinutesChange = {}, onOpenSystemSettings = {}, onEraseAll = {},
        )
    }

    // Bienvenida: un PNG por paso.
    @Test
    fun onboarding() = OnboardingStep.entries.forEachIndexed { i, step ->
        capture("00${'a' + i}_bienvenida_${step.name.lowercase()}", tab = null) {
            OnboardingContent(OnboardingUiState(step = step, name = if (step == OnboardingStep.NAME) "Ana" else ""), {}, {}, {}, {})
        }
    }

    @Test
    fun shop() = capture("06_tienda", tab = null) { ShopScreen(onBack = {}) }
}

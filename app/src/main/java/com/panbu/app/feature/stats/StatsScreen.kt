package com.panbu.app.feature.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.domain.model.CalmBar
import com.panbu.app.domain.model.MoodDistribution
import com.panbu.app.domain.model.PeriodStats
import com.panbu.app.domain.model.StatsCalculator
import com.panbu.app.domain.model.StatsPeriod
import com.panbu.app.ui.components.IconBubble
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.PrimaryPillButton
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.SegmentedSelector
import com.panbu.app.ui.components.TrendText
import com.panbu.app.ui.components.charts.BarEntry
import com.panbu.app.ui.components.charts.CalmBarChart
import com.panbu.app.ui.components.charts.DonutChart
import com.panbu.app.ui.components.charts.DonutSlice
import com.panbu.app.ui.components.color
import com.panbu.app.ui.components.formatDuration
import com.panbu.app.ui.components.labelRes
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs

/** Estadísticas (docs/02-producto.md §2): tiempo de calma, estados de ánimo y tiempo de pantalla. */
@Composable
fun StatsScreen(
    onOpenBreathing: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StatsContent(uiState = uiState, onPeriodSelected = viewModel::onPeriodSelected, onOpenBreathing = onOpenBreathing)
}

/** Sin estado: la usan StatsScreen, el @Preview y las capturas de pantalla. */
@Composable
internal fun StatsContent(
    uiState: StatsUiState,
    onPeriodSelected: (StatsPeriod) -> Unit,
    onOpenBreathing: () -> Unit,
) {
    PanbuScreen {
        ScreenTitle(text = stringResource(R.string.stats_title), iconRes = R.drawable.ic_nav_stats)
        SegmentedSelector(
            options = StatsPeriod.entries,
            selected = uiState.period,
            onSelect = onPeriodSelected,
            label = { stringResource(it.labelRes()) },
            groupDescription = stringResource(R.string.stats_periods_cd),
        )

        val stats = uiState.stats ?: return@PanbuScreen // Leyendo la base: solo un instante.

        Text(
            text = spanText(stats),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (stats.isEmpty) {
            EmptyStatsCard(onOpenBreathing)
        } else {
            CalmTimeCard(stats)
            MoodCard(stats.moods)
        }
        ScreenTimeCard()
    }
}

// --- Tarjetas ---

@Composable
private fun CalmTimeCard(stats: PeriodStats) {
    PanbuCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(text = stringResource(R.string.stats_calm_time), style = MaterialTheme.typography.titleSmall)
                Text(text = formatDuration(stats.calmTotal), style = MaterialTheme.typography.displaySmall)
                stats.calmChangePercent?.let { CalmChange(it, stats.period) }
            }
            IconBubble(R.drawable.ic_panbu_face, tinted = false)
        }
        Spacer(Modifier.height(PanbuSpacing.md))
        if (stats.hasCalm) {
            val entries = stats.bars.map { BarEntry(barLabel(it, stats.period), it.calm, highlighted = stats.today in it.span) }
            CalmBarChart(entries = entries, contentDescription = chartDescription(entries))
        } else {
            Text(
                text = stringResource(
                    when (stats.period) {
                        StatsPeriod.WEEK -> R.string.stats_no_calm_week
                        StatsPeriod.MONTH -> R.string.stats_no_calm_month
                        StatsPeriod.TOTAL -> R.string.stats_no_calm_total
                    },
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** "↑ 32% que la semana anterior": verde si sube, gris si baja (nunca rojo). */
@Composable
private fun CalmChange(percent: Int, period: StatsPeriod) {
    val previous = stringResource(
        if (period == StatsPeriod.MONTH) R.string.stats_previous_month else R.string.stats_previous_week,
    )
    val text = when {
        percent > 0 -> stringResource(R.string.stats_change_up, percent, previous)
        percent < 0 -> stringResource(R.string.stats_change_down, abs(percent), previous)
        else -> stringResource(R.string.stats_change_same, previous)
    }
    TrendText(text = text, improved = percent > 0)
}

@Composable
private fun MoodCard(moods: MoodDistribution?) {
    PanbuCard(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.stats_moods_title), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(PanbuSpacing.sm))
        if (moods == null) {
            Text(
                text = stringResource(R.string.stats_no_moods),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@PanbuCard
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            val top = moods.mostFrequent
            DonutChart(
                slices = moods.shares.map { DonutSlice(it.count.toFloat(), it.mood.color()) },
                modifier = Modifier
                    .size(136.dp)
                    .clearAndSetSemantics {}, // la leyenda ya lo dice en texto
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ic_panbu_face),
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(text = stringResource(R.string.stats_percent, top.percent), style = MaterialTheme.typography.titleMedium)
                    Text(text = stringResource(top.mood.labelRes()), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.width(PanbuSpacing.lg))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(PanbuSpacing.sm)) {
                moods.shares.forEach { share ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.semantics(mergeDescendants = true) {},
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(share.mood.color()),
                        )
                        Spacer(Modifier.width(PanbuSpacing.sm))
                        Text(
                            text = stringResource(share.mood.labelRes()),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = stringResource(R.string.stats_percent, share.percent),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(PanbuSpacing.sm))
        Text(
            text = pluralStringResource(R.plurals.stats_moods_count, moods.total, moods.total),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** El tiempo de pantalla de noche llega en la Fase 2 (necesita un permiso). Mientras tanto, no se inventa. */
@Composable
private fun ScreenTimeCard() {
    PanbuCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(R.drawable.ic_moon)
            Spacer(Modifier.width(PanbuSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(text = stringResource(R.string.stats_screen_time_title), style = MaterialTheme.typography.titleSmall)
                Text(
                    text = stringResource(R.string.soon),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.stats_screen_time_soon),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Sin registros en el periodo: un mensaje amable y una acción, nunca gráficos en cero. */
@Composable
private fun EmptyStatsCard(onOpenBreathing: () -> Unit) {
    PanbuCard(modifier = Modifier.fillMaxWidth(), containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ill_panda_heart),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
            Spacer(Modifier.height(PanbuSpacing.sm))
            Text(
                text = stringResource(R.string.stats_empty),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(PanbuSpacing.lg))
            PrimaryPillButton(text = stringResource(R.string.stats_empty_action), onClick = onOpenBreathing)
        }
    }
}

// --- Textos ---

private fun StatsPeriod.labelRes(): Int = when (this) {
    StatsPeriod.WEEK -> R.string.stats_period_week
    StatsPeriod.MONTH -> R.string.stats_period_month
    StatsPeriod.TOTAL -> R.string.stats_period_total
}

/** "9 – 15 nov", "Noviembre de 2026" o "Desde el 9 de noviembre de 2026". */
@Composable
private fun spanText(stats: PeriodStats): String {
    val monthsShort = stringArrayResource(R.array.months_short)
    val monthsFull = stringArrayResource(R.array.months_full)
    val start = stats.span.start
    val last = stats.span.lastDay
    return when (stats.period) {
        StatsPeriod.WEEK -> stringResource(
            R.string.stats_span_days,
            start.dayOfMonth, monthsShort[start.monthValue - 1], last.dayOfMonth, monthsShort[last.monthValue - 1],
        )
        StatsPeriod.MONTH -> stringResource(
            R.string.stats_span_month,
            monthsFull[start.monthValue - 1].replaceFirstChar { it.uppercase() },
            start.year,
        )
        StatsPeriod.TOTAL -> stringResource(
            R.string.stats_span_since,
            start.dayOfMonth, monthsFull[start.monthValue - 1], start.year,
        )
    }
}

/** "Lun" (Semana), "3–9" (Mes) o "nov" (Total). */
@Composable
private fun barLabel(bar: CalmBar, period: StatsPeriod): String {
    val start = bar.span.start
    return when (period) {
        StatsPeriod.WEEK -> stringArrayResource(R.array.weekdays_short)[start.dayOfWeek.value - 1]
        StatsPeriod.MONTH -> {
            val last = bar.span.lastDay
            if (last == start) start.dayOfMonth.toString() else stringResource(R.string.stats_bar_days, start.dayOfMonth, last.dayOfMonth)
        }
        StatsPeriod.TOTAL -> stringArrayResource(R.array.months_short)[start.monthValue - 1]
    }
}

/** Resumen para TalkBack: "Tiempo de calma: Lun, 15 min; Mar, 0 min; …". */
@Composable
private fun chartDescription(entries: List<BarEntry>): String {
    val items = entries.map { stringResource(R.string.stats_chart_item_cd, it.label, formatDuration(it.value)) }
    return stringResource(R.string.stats_chart_cd, items.joinToString("; "))
}

// --- Vista previa (datos de ejemplo solo aquí, nunca en la app) ---

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 1300)
@Composable
private fun StatsContentPreview() {
    val today = LocalDate.of(2026, 11, 12)
    val stats = StatsCalculator.build(StatsPeriod.WEEK, today, ZoneId.of("UTC"), emptyList(), emptyList())
    PanbuTheme {
        StatsContent(uiState = StatsUiState(stats = stats), onPeriodSelected = {}, onOpenBreathing = {})
    }
}

package com.panbu.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.domain.model.DefaultGoals
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.model.GoalProgress
import com.panbu.app.domain.model.Mood
import com.panbu.app.ui.components.ActionRow
import com.panbu.app.ui.components.ChoiceChip
import com.panbu.app.ui.components.GoalRow
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.SectionTitle
import com.panbu.app.ui.components.TrendText
import com.panbu.app.ui.components.color
import com.panbu.app.ui.components.formatDuration
import com.panbu.app.ui.components.labelRes
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Duration

/** Inicio: cómo se siente la persona, su Panbu hoy y sus objetivos (docs/02-producto.md §1). */
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onOpenBreathing: () -> Unit,
    onOpenMoment: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onMoodSelected = viewModel::onMoodSelected,
        onOpenSettings = onOpenSettings,
        onOpenBreathing = onOpenBreathing,
        onOpenMoment = onOpenMoment,
    )
}

/** Sin estado: la usan HomeScreen, el @Preview y las capturas de pantalla. */
@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    onMoodSelected: (Mood) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenBreathing: () -> Unit = {},
    onOpenMoment: () -> Unit = {},
) {
    PanbuScreen(
        showTagline = true,
        headerActions = {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    painter = painterResource(R.drawable.ic_nav_settings),
                    contentDescription = stringResource(R.string.nav_settings),
                )
            }
        },
    ) {
        GreetingCard(userName = uiState.userName, todayMood = uiState.todayMood, onMoodSelected = onMoodSelected)

        SectionTitle(stringResource(R.string.home_panbu_today))
        PanbuTodayCard(
            calmToday = uiState.calmToday,
            calmYesterday = uiState.calmYesterday,
            onOpenBreathing = onOpenBreathing,
            onOpenMoment = onOpenMoment,
        )

        SectionTitle(stringResource(R.string.home_goals_today))
        if (uiState.goals.isEmpty()) {
            Text(
                text = stringResource(R.string.home_no_goals),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            uiState.goals.forEach { GoalRow(it) }
        }
    }
}

@Composable
private fun GreetingCard(userName: String?, todayMood: Mood?, onMoodSelected: (Mood) -> Unit) {
    PanbuCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (userName == null) {
                        stringResource(R.string.home_greeting)
                    } else {
                        stringResource(R.string.home_greeting_name, userName)
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = stringResource(R.string.home_how_do_you_feel), style = MaterialTheme.typography.bodyLarge)
            }
            Image(
                painter = painterResource(R.drawable.ill_panda_heart),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
        }
        Spacer(Modifier.height(PanbuSpacing.sm))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm),
        ) {
            Mood.entries.forEach { mood ->
                ChoiceChip(
                    text = stringResource(mood.labelRes()),
                    selected = mood == todayMood,
                    onClick = { onMoodSelected(mood) },
                    dotColor = mood.color(),
                )
            }
        }
        Spacer(Modifier.height(PanbuSpacing.sm))
        Text(
            text = if (todayMood == null) {
                stringResource(R.string.home_no_mood_yet)
            } else {
                stringResource(R.string.home_mood_saved, stringResource(todayMood.labelRes()))
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun PanbuTodayCard(
    calmToday: Duration,
    calmYesterday: Duration,
    onOpenBreathing: () -> Unit,
    onOpenMoment: () -> Unit,
) {
    PanbuCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_time_with_you),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = formatDuration(calmToday), style = MaterialTheme.typography.headlineSmall)
        CalmComparison(calmToday = calmToday, calmYesterday = calmYesterday)
        if (calmToday.isZero) {
            Spacer(Modifier.height(PanbuSpacing.xs))
            Text(
                text = stringResource(R.string.home_no_calm_yet),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(PanbuSpacing.sm))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ActionRow(
            iconRes = R.drawable.ic_breath,
            title = stringResource(R.string.home_calm_mode),
            subtitle = stringResource(R.string.home_calm_mode_desc),
            onClick = onOpenBreathing,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ActionRow(
            iconRes = R.drawable.ic_heart,
            title = stringResource(R.string.home_moment),
            subtitle = stringResource(R.string.home_moment_desc),
            onClick = onOpenMoment,
        )
    }
}

/** "↑ 45 min más que ayer". Solo si ayer hubo tiempo de calma; nunca en rojo (guardrail 5). */
@Composable
private fun CalmComparison(calmToday: Duration, calmYesterday: Duration) {
    if (calmYesterday.isZero) return
    val diffMinutes = calmToday.toMinutes() - calmYesterday.toMinutes()
    val diffText = formatDuration(Duration.ofMinutes(kotlin.math.abs(diffMinutes)))
    val text = when {
        diffMinutes > 0 -> stringResource(R.string.home_vs_yesterday_more, diffText)
        diffMinutes < 0 -> stringResource(R.string.home_vs_yesterday_less, diffText)
        else -> stringResource(R.string.home_vs_yesterday_same)
    }
    TrendText(text = text, improved = diffMinutes > 0)
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 1200)
@Composable
private fun HomeContentPreview() {
    PanbuTheme {
        HomeContent(
            uiState = HomeUiState(
                todayMood = Mood.CALM,
                calmToday = Duration.ofMinutes(135),
                calmYesterday = Duration.ofMinutes(90),
                goals = DefaultGoals.all.mapIndexed { i, goal -> GoalItem(goal, GoalProgress(goal, current = 2 - i)) },
            ),
            onMoodSelected = {},
            onOpenSettings = {},
        )
    }
}

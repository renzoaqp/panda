package com.panbu.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.domain.model.Mood
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.SectionTitle
import com.panbu.app.ui.components.UnderConstructionCard
import com.panbu.app.ui.components.color
import com.panbu.app.ui.components.labelRes
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

/** Inicio: cómo se siente la persona, su Panbu hoy y sus objetivos (docs/02-producto.md §1). */
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onMoodSelected = viewModel::onMoodSelected,
        onOpenSettings = onOpenSettings,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onMoodSelected: (Mood) -> Unit,
    onOpenSettings: () -> Unit,
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
        GreetingCard(todayMood = uiState.todayMood, onMoodSelected = onMoodSelected)

        SectionTitle(stringResource(R.string.home_panbu_today))
        UnderConstructionCard(phase = 1, description = stringResource(R.string.home_panbu_today_placeholder))

        SectionTitle(stringResource(R.string.home_goals_today))
        UnderConstructionCard(phase = 1, description = stringResource(R.string.home_goals_placeholder))
    }
}

@Composable
private fun GreetingCard(todayMood: Mood?, onMoodSelected: (Mood) -> Unit) {
    PanbuCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(text = stringResource(R.string.home_greeting), style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(R.string.home_how_do_you_feel), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(PanbuSpacing.md))
        MoodSelector(selected = todayMood, onSelect = onMoodSelected)
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
private fun MoodSelector(selected: Mood?, onSelect: (Mood) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm),
    ) {
        Mood.entries.forEach { mood ->
            MoodChip(mood = mood, selected = mood == selected, onClick = { onSelect(mood) })
        }
    }
}

@Composable
private fun MoodChip(mood: Mood, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .heightIn(min = PanbuSpacing.minTouchTarget)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            )
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = PanbuSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(mood.color()),
        )
        Spacer(Modifier.width(PanbuSpacing.sm))
        Text(text = stringResource(mood.labelRes()), style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun HomeContentPreview() {
    PanbuTheme {
        HomeContent(uiState = HomeUiState(todayMood = Mood.CALM), onMoodSelected = {}, onOpenSettings = {})
    }
}

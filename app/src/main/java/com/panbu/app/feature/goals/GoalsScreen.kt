package com.panbu.app.feature.goals

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import com.panbu.app.ui.components.GoalRow
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

/** Metas (docs/02-producto.md §3). Por ahora, las metas iniciales con progreso automático. */
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GoalsContent(uiState)
}

@Composable
internal fun GoalsContent(uiState: GoalsUiState) {
    PanbuScreen {
        ScreenTitle(
            text = stringResource(R.string.goals_title),
            iconRes = R.drawable.ic_nav_goals,
            trailing = {
                // APK 0.3+: abre "Crear meta".
                FilledTonalIconButton(onClick = {}, enabled = false) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.goals_add),
                    )
                }
            },
        )
        Text(
            text = stringResource(R.string.goals_auto_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        uiState.goals?.forEach { GoalRow(it) }
        Text(
            text = stringResource(R.string.goals_custom_soon),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        MotivationBanner()
    }
}

@Composable
private fun MotivationBanner() {
    PanbuCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.goals_motivation_title),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(R.string.goals_motivation_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.width(PanbuSpacing.md))
            Image(
                painter = painterResource(R.drawable.ill_panda_heart),
                contentDescription = null,
                modifier = Modifier.size(88.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun GoalsContentPreview() {
    PanbuTheme {
        GoalsContent(
            GoalsUiState(goals = DefaultGoals.all.map { GoalItem(it, GoalProgress(it, current = 1)) }),
        )
    }
}

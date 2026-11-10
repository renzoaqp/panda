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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panbu.app.R
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.UnderConstructionCard
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

/** Metas (docs/02-producto.md §3). Crear/editar metas llega en la Fase 1. */
@Composable
fun GoalsScreen() {
    PanbuScreen {
        ScreenTitle(
            text = stringResource(R.string.goals_title),
            iconRes = R.drawable.ic_nav_goals,
            trailing = {
                // Fase 1: abre "Crear meta".
                FilledTonalIconButton(onClick = {}, enabled = false) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.goals_add),
                    )
                }
            },
        )
        UnderConstructionCard(phase = 1, description = stringResource(R.string.goals_placeholder))
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
            Image(
                painter = painterResource(R.drawable.ic_panbu_face),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.width(PanbuSpacing.lg))
            Column {
                Text(
                    text = stringResource(R.string.goals_motivation_title),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(R.string.goals_motivation_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun GoalsScreenPreview() {
    PanbuTheme { GoalsScreen() }
}

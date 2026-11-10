package com.panbu.app.feature.calm

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

private data class CalmActivity(@StringRes val title: Int, @StringRes val description: Int)

private val calmActivities = listOf(
    CalmActivity(R.string.calm_breathe_title, R.string.calm_breathe_desc),
    CalmActivity(R.string.calm_moment_title, R.string.calm_moment_desc),
    CalmActivity(R.string.calm_messages_title, R.string.calm_messages_desc),
    CalmActivity(R.string.calm_sounds_title, R.string.calm_sounds_desc),
    CalmActivity(R.string.calm_journal_title, R.string.calm_journal_desc),
    CalmActivity(R.string.calm_rest_title, R.string.calm_rest_desc),
)

/**
 * Panbu, tu espacio de calma (docs/02-producto.md §4). No está en el mockup: se diseña en la Fase 0
 * y cada actividad se implementa en la Fase 1.
 */
@Composable
fun CalmSpaceScreen(onOpenShop: () -> Unit) {
    PanbuScreen {
        ScreenTitle(text = stringResource(R.string.calm_title), iconRes = R.drawable.ic_nav_panbu)

        // Fase 1: un toque → 1 minuto de respiración suave (BreathingPattern.Gentle).
        Button(
            onClick = {},
            enabled = false,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
        ) {
            Text(text = stringResource(R.string.calm_need_a_moment), style = MaterialTheme.typography.labelLarge)
        }
        Text(
            text = stringResource(R.string.placeholder_phase, 1),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        calmActivities.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.md),
            ) {
                pair.forEach { activity ->
                    CalmActivityCard(
                        activity = activity,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
            }
        }

        // Única entrada a la tienda (guardrail 7: la tienda no aparece en los flujos de calma).
        PanbuCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onOpenShop,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_panbu_face),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.width(PanbuSpacing.lg))
                Column {
                    Text(
                        text = stringResource(R.string.calm_get_panbu_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = stringResource(R.string.calm_get_panbu_desc),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CalmActivityCard(activity: CalmActivity, modifier: Modifier = Modifier) {
    PanbuCard(modifier = modifier) {
        Text(text = stringResource(activity.title), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(PanbuSpacing.xs))
        Text(
            text = stringResource(activity.description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun CalmSpaceScreenPreview() {
    PanbuTheme { CalmSpaceScreen(onOpenShop = {}) }
}

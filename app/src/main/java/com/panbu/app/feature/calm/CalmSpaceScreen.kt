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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.panbu.app.ui.components.PrimaryPillButton
import com.panbu.app.ui.components.HelpLineCard
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

/** Una actividad del espacio de calma. Sin [onClick] todavía no está disponible ("Próximamente"). */
private data class CalmActivity(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val onClick: (() -> Unit)? = null,
)

/**
 * Panbu, tu espacio de calma (docs/02-producto.md §4). No está en el mockup; se diseñó con el mismo estilo.
 * APK 0.2: "Necesito un momento de calma" y "Respira con Panbu" funcionan; el resto llega en las versiones siguientes.
 */
@Composable
fun CalmSpaceScreen(
    onNeedCalm: () -> Unit,
    onOpenBreathing: () -> Unit,
    onOpenShop: () -> Unit,
) {
    val activities = listOf(
        CalmActivity(R.string.calm_breathe_title, R.string.calm_breathe_desc, onOpenBreathing),
        CalmActivity(R.string.calm_moment_title, R.string.calm_moment_desc),
        CalmActivity(R.string.calm_messages_title, R.string.calm_messages_desc),
        CalmActivity(R.string.calm_sounds_title, R.string.calm_sounds_desc),
        CalmActivity(R.string.calm_journal_title, R.string.calm_journal_desc),
        CalmActivity(R.string.calm_rest_title, R.string.calm_rest_desc),
    )

    PanbuScreen {
        ScreenTitle(text = stringResource(R.string.calm_title), iconRes = R.drawable.ic_nav_panbu)

        // Siempre visible y sin pasos previos: un toque → 1 minuto de respiración suave.
        PrimaryPillButton(text = stringResource(R.string.calm_need_a_moment), onClick = onNeedCalm)

        activities.chunked(2).forEach { pair ->
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
                    painter = painterResource(R.drawable.ill_panda_heart),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
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

        // Siempre hay una salida a ayuda humana (guardrail 2).
        HelpLineCard()
    }
}

@Composable
private fun CalmActivityCard(activity: CalmActivity, modifier: Modifier = Modifier) {
    PanbuCard(modifier = modifier, onClick = activity.onClick) {
        Text(text = stringResource(activity.title), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(PanbuSpacing.xs))
        Text(
            text = stringResource(activity.description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (activity.onClick == null) {
            Spacer(Modifier.height(PanbuSpacing.xs))
            Text(
                text = stringResource(R.string.soon),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 1000)
@Composable
private fun CalmSpaceScreenPreview() {
    PanbuTheme { CalmSpaceScreen(onNeedCalm = {}, onOpenBreathing = {}, onOpenShop = {}) }
}

package com.panbu.app.feature.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.panbu.app.R
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

/** Opciones de la guía (sección 8). "Mi cuenta" incluye cerrar sesión y borrar la cuenta (ADR-0004). */
private val settingsItems = listOf(
    R.string.settings_account,
    R.string.settings_profile,
    R.string.settings_notifications,
    R.string.settings_rest_schedule,
    R.string.settings_reminders,
    R.string.settings_sounds,
    R.string.settings_theme,
    R.string.settings_privacy,
    R.string.settings_help,
)

/** Ajustes (docs/02-producto.md §6). No está en el mockup; cada opción se implementa en la Fase 1. */
@Composable
fun SettingsScreen() {
    PanbuScreen {
        ScreenTitle(text = stringResource(R.string.settings_title), iconRes = R.drawable.ic_nav_settings)

        PanbuCard(modifier = Modifier.fillMaxWidth()) {
            settingsItems.forEachIndexed { index, item ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = stringResource(item),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = PanbuSpacing.minTouchTarget)
                        .wrapContentHeight(align = Alignment.CenterVertically),
                )
            }
        }

        Text(
            text = stringResource(R.string.placeholder_phase, 1),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun SettingsScreenPreview() {
    PanbuTheme { SettingsScreen() }
}

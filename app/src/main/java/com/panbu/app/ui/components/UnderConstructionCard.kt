package com.panbu.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.panbu.app.R
import com.panbu.app.ui.theme.PanbuSpacing

/**
 * Marca una parte de la pantalla que todavía no está construida y dice en qué fase del plan se hace.
 * Se elimina a medida que se completan las fases (docs/01-plan-de-trabajo.md).
 */
@Composable
fun UnderConstructionCard(
    phase: Int,
    description: String,
    modifier: Modifier = Modifier,
) {
    PanbuCard(modifier = modifier.fillMaxWidth()) {
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(PanbuSpacing.sm))
        Text(
            text = stringResource(R.string.placeholder_phase, phase),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

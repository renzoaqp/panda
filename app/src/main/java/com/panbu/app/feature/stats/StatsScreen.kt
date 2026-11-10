package com.panbu.app.feature.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.panbu.app.R
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.UnderConstructionCard
import com.panbu.app.ui.theme.PanbuTheme

/** Estadísticas (docs/02-producto.md §2). Diseño en la Fase 0, datos reales en la Fase 2. */
@Composable
fun StatsScreen() {
    PanbuScreen {
        ScreenTitle(text = stringResource(R.string.stats_title), iconRes = R.drawable.ic_nav_stats)
        UnderConstructionCard(phase = 2, description = stringResource(R.string.stats_placeholder))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun StatsScreenPreview() {
    PanbuTheme { StatsScreen() }
}

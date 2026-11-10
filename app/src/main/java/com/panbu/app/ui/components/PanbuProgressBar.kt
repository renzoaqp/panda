package com.panbu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.panbu.app.ui.theme.PanbuTheme

/**
 * Barra de progreso redondeada como la del mockup.
 *
 * @param progress de 0 a 1. Valores fuera de rango o NaN se ajustan.
 * @param color usar `PanbuTheme.extraColors.success` para metas cumplidas y
 * `PanbuTheme.extraColors.overLimit` para límites superados.
 */
@Composable
fun PanbuProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = PanbuTheme.extraColors.progress,
    trackColor: Color = PanbuTheme.extraColors.progressTrack,
    height: Dp = 8.dp,
) {
    val value = if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f) },
    ) {
        if (value > 0f) {
            Box(
                Modifier
                    .fillMaxWidth(value)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

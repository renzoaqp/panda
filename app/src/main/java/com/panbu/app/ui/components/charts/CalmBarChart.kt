package com.panbu.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.panbu.app.R
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Duration
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/** Una barra: su etiqueta ("Lun", "3–9", "nov"), el tiempo y si es la de hoy (se resalta). */
data class BarEntry(val label: String, val value: Duration, val highlighted: Boolean = false)

/**
 * Gráfico de barras del tiempo de calma (Estadísticas). Eje con pasos redondos ([MinutesAxis]), líneas guía
 * suaves y barras con las esquinas de arriba redondeadas, como en el mockup.
 *
 * Para TalkBack es un solo elemento con [contentDescription] (el resumen en texto de todas las barras).
 */
@Composable
fun CalmBarChart(
    entries: List<BarEntry>,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    if (entries.isEmpty()) return
    val axis = remember(entries) { MinutesAxis.forMaximum(entries.maxOf { it.value }) }
    val axisLabels = axis.ticks.map { axisLabel(it) }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    val gridColor = MaterialTheme.colorScheme.outline
    val barColor = PanbuTheme.extraColors.progress
    val todayColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier
            .fillMaxWidth()
            .height(180.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        val yLabels = axisLabels.map { textMeasurer.measure(it, labelStyle) }
        val xLabels = entries.map { textMeasurer.measure(it.label, labelStyle) }
        val gap = 8.dp.toPx()
        val yLabelWidth = yLabels.maxOf { it.size.width }.toFloat()
        val xLabelHeight = xLabels.maxOf { it.size.height }.toFloat()

        val plotLeft = yLabelWidth + gap
        val plotTop = yLabels.last().size.height / 2f
        val plotBottom = size.height - xLabelHeight - gap
        val plotHeight = plotBottom - plotTop

        // Líneas guía y etiquetas del eje vertical.
        axis.ticks.indices.forEach { i ->
            val y = plotBottom - plotHeight * i / axis.steps
            drawLine(gridColor, Offset(plotLeft, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            val label = yLabels[i]
            drawText(label, topLeft = Offset(yLabelWidth - label.size.width, y - label.size.height / 2f))
        }

        // Barras y etiquetas de abajo. Si las etiquetas no caben (muchas barras o letra grande),
        // se muestra una de cada N, siempre incluida la última.
        val slot = (size.width - plotLeft) / entries.size
        val barWidth = min(slot * 0.55f, 28.dp.toPx())
        val widestLabel = xLabels.maxOf { it.size.width } + 4.dp.toPx()
        val labelEvery = max(1, ceil(widestLabel / slot).toInt())
        val radius = min(6.dp.toPx(), barWidth / 2)

        entries.forEachIndexed { i, entry ->
            val centerX = plotLeft + slot * (i + 0.5f)
            val height = when {
                entry.value.isZero -> 0f
                // Una barra con muy poco tiempo se ve igual (al menos 3 dp), para que no parezca vacía.
                else -> max(plotHeight * axis.fractionOf(entry.value), 3.dp.toPx())
            }
            if (height > 0f) {
                val bar = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = centerX - barWidth / 2,
                            top = plotBottom - height,
                            right = centerX + barWidth / 2,
                            bottom = plotBottom,
                            topLeftCornerRadius = CornerRadius(radius),
                            topRightCornerRadius = CornerRadius(radius),
                        ),
                    )
                }
                drawPath(bar, color = if (entry.highlighted) todayColor else barColor)
            }
            if ((entries.lastIndex - i) % labelEvery == 0) {
                val label = xLabels[i]
                drawText(label, topLeft = Offset(centerX - label.size.width / 2f, plotBottom + gap))
            }
        }
    }
}

/** "0", "15 min", "1 h", "1 h 30 min". */
@Composable
private fun axisLabel(minutes: Long): String = when {
    minutes == 0L -> "0"
    minutes % 60 == 0L -> stringResource(R.string.duration_h, minutes / 60)
    minutes > 60 -> stringResource(R.string.duration_h_min, minutes / 60, minutes % 60)
    else -> stringResource(R.string.duration_min, minutes)
}

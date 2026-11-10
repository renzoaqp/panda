package com.panbu.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/** Una porción de la dona: su peso (por ejemplo, la cantidad de registros) y su color. */
data class DonutSlice(val value: Float, val color: Color)

/**
 * Gráfico de dona ("¿Cómo te sientes?"). Empieza arriba y va en el sentido del reloj, con una línea fina
 * del color de la tarjeta entre porción y porción. En el centro se dibuja [center].
 *
 * Es decorativo para TalkBack: la leyenda de al lado ya dice lo mismo en texto.
 */
@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    thickness: Dp = 24.dp,
    center: @Composable BoxScope.() -> Unit = {},
) {
    val separatorColor = MaterialTheme.colorScheme.surface
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val total = slices.sumOf { it.value.toDouble() }.toFloat()
            if (total <= 0f) return@Canvas
            val stroke = thickness.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            val arcSize = Size(diameter, diameter)

            var start = -90f
            val boundaries = mutableListOf<Float>()
            slices.filter { it.value > 0f }.forEach { slice ->
                val sweep = 360f * slice.value / total
                drawArc(slice.color, start, sweep, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(stroke))
                boundaries += start
                start += sweep
            }

            // Separadores entre porciones (solo si hay más de una).
            if (boundaries.size > 1) {
                val center = Offset(size.width / 2, size.height / 2)
                val inner = diameter / 2 - stroke / 2
                val outer = diameter / 2 + stroke / 2
                boundaries.forEach { angle ->
                    val radians = Math.toRadians(angle.toDouble())
                    val direction = Offset(cos(radians).toFloat(), sin(radians).toFloat())
                    drawLine(
                        color = separatorColor,
                        start = center + direction * inner,
                        end = center + direction * outer,
                        strokeWidth = 2.dp.toPx(),
                    )
                }
            }
        }
        center()
    }
}

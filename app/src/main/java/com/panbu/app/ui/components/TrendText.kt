package com.panbu.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.panbu.app.ui.theme.PanbuTheme

/**
 * Comparación con el periodo anterior ("↑ 32% que la semana anterior", "↑ 45 min más que ayer").
 *
 * [improved] dice si el cambio es bueno para la persona: más tiempo de calma es una mejora, pero en el
 * tiempo de pantalla de noche la mejora es que baje. Una mejora va en verde; todo lo demás, en gris
 * neutro. **Nunca en rojo** (docs/06-guardrails.md, regla 5).
 */
@Composable
fun TrendText(text: String, improved: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = if (improved) PanbuTheme.extraColors.positiveText else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

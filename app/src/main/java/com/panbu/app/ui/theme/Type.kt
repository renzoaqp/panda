package com.panbu.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Fase 0: reemplazar por Nunito (agregar los .ttf a res/font, licencia OFL).
 * Ver docs/04-diseno-visual.md, sección "Tipografía".
 */
val PanbuFontFamily: FontFamily = FontFamily.SansSerif

private fun panbuStyle(weight: FontWeight, size: Int, lineHeight: Int) = TextStyle(
    fontFamily = PanbuFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

val PanbuTypography = Typography(
    displaySmall = panbuStyle(FontWeight.Bold, 32, 40), // números grandes: "2 h 15 min"
    headlineSmall = panbuStyle(FontWeight.Bold, 24, 32), // título de pantalla
    titleLarge = panbuStyle(FontWeight.Bold, 22, 28), // saludo
    titleMedium = panbuStyle(FontWeight.SemiBold, 18, 24), // título de sección
    titleSmall = panbuStyle(FontWeight.SemiBold, 16, 22), // título de tarjeta
    bodyLarge = panbuStyle(FontWeight.Normal, 16, 24),
    bodyMedium = panbuStyle(FontWeight.Normal, 14, 20),
    bodySmall = panbuStyle(FontWeight.Normal, 12, 16),
    labelLarge = panbuStyle(FontWeight.SemiBold, 14, 20), // botones, chips
    labelMedium = panbuStyle(FontWeight.Medium, 12, 16),
    labelSmall = panbuStyle(FontWeight.Medium, 11, 16), // barra inferior
)

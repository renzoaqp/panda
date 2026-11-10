package com.panbu.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de Panbu. El origen y el uso de cada color están en docs/04-diseno-visual.md.
 *
 * Las pantallas NO usan estos valores directamente: usan MaterialTheme.colorScheme
 * o PanbuTheme.extraColors, para que el tema oscuro funcione.
 */
object PanbuPalette {
    // Guía del cliente (00info/Guia_de_la_aplicacion_Panbu.pdf, sección 9)
    val Lavender = Color(0xFFE8DDF5)
    val Rose = Color(0xFFF7E6EF)
    val Cream = Color(0xFFF8F5EF)
    val Sage = Color(0xFFA9C8B0)

    // Medidos sobre el mockup (00info/WhatsApp Image 2026-11-09 at 22.18.13.jpeg)
    val Purple = Color(0xFF7B65AE)
    val PurpleDeep = Color(0xFF6B52A3)
    val PurpleSoft = Color(0xFF9E8BCD)
    val SageStrong = Color(0xFF8DB795)
    val SageText = Color(0xFF4C8165)
    val RoseStrong = Color(0xFFE3A6BE)
    val Ink = Color(0xFF24212E)
    val InkSoft = Color(0xFF5F5870) // mockup #756E87, oscurecido para contraste AA
    val Card = Color(0xFFFFFDFA)
    val Outline = Color(0xFFECE6F3)
    val Track = Color(0xFFEDE7F6)

    // Tema oscuro (borrador; se valida en la Fase 1)
    val NightBackground = Color(0xFF1E1B26)
    val NightCard = Color(0xFF2A2635)
    val NightLavender = Color(0xFF3B3450)
    val NightPurple = Color(0xFFC9B8F0)
    val NightInk = Color(0xFFEDE8F5)
    val NightInkSoft = Color(0xFFB5ADC6)
    val NightOutline = Color(0xFF3F3950)
    val NightRose = Color(0xFF8A5A6E)
}

/** Color de cada estado de ánimo (igual en tema claro y oscuro). */
object MoodPalette {
    val Calm = Color(0xFFB19DE2)
    val Anxious = Color(0xFFA9C4EB)
    val Stressed = Color(0xFFF7C9A5)
    val Sad = Color(0xFFF2B8C6)
    val Happy = Color(0xFFF5DC8C)
}

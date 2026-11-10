package com.panbu.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PanbuPalette.Purple,
    onPrimary = Color.White,
    primaryContainer = PanbuPalette.Lavender,
    onPrimaryContainer = PanbuPalette.Ink,
    secondary = PanbuPalette.PurpleSoft,
    onSecondary = PanbuPalette.Ink,
    secondaryContainer = PanbuPalette.Rose,
    onSecondaryContainer = PanbuPalette.Ink,
    tertiary = PanbuPalette.SageStrong,
    onTertiary = PanbuPalette.Ink,
    tertiaryContainer = PanbuPalette.Sage,
    onTertiaryContainer = PanbuPalette.Ink,
    background = PanbuPalette.Cream,
    onBackground = PanbuPalette.Ink,
    surface = PanbuPalette.Card,
    onSurface = PanbuPalette.Ink,
    surfaceVariant = PanbuPalette.Lavender,
    onSurfaceVariant = PanbuPalette.InkSoft,
    surfaceContainer = PanbuPalette.Card,
    outline = PanbuPalette.Outline,
    outlineVariant = PanbuPalette.Outline,
)

private val DarkColors = darkColorScheme(
    primary = PanbuPalette.NightPurple,
    onPrimary = PanbuPalette.NightBackground,
    primaryContainer = PanbuPalette.NightLavender,
    onPrimaryContainer = PanbuPalette.NightInk,
    secondary = PanbuPalette.PurpleSoft,
    onSecondary = PanbuPalette.NightBackground,
    secondaryContainer = PanbuPalette.NightLavender,
    onSecondaryContainer = PanbuPalette.NightInk,
    tertiary = PanbuPalette.Sage,
    onTertiary = PanbuPalette.NightBackground,
    tertiaryContainer = PanbuPalette.SageText,
    onTertiaryContainer = PanbuPalette.NightInk,
    background = PanbuPalette.NightBackground,
    onBackground = PanbuPalette.NightInk,
    surface = PanbuPalette.NightCard,
    onSurface = PanbuPalette.NightInk,
    surfaceVariant = PanbuPalette.NightLavender,
    onSurfaceVariant = PanbuPalette.NightInkSoft,
    surfaceContainer = PanbuPalette.NightCard,
    outline = PanbuPalette.NightOutline,
    outlineVariant = PanbuPalette.NightOutline,
)

/** Colores propios de Panbu que Material 3 no tiene. */
@Immutable
data class PanbuExtraColors(
    /** Barras de progreso y gráficos. */
    val progress: Color,
    /** Fondo de las barras de progreso. */
    val progressTrack: Color,
    /** Meta cumplida. */
    val success: Color,
    /** Texto de comparaciones positivas ("↑ 32 %"). */
    val positiveText: Color,
    /** Límite superado: rosa suave, nunca rojo (docs/06-guardrails.md, regla 5). */
    val overLimit: Color,
)

private val LightExtraColors = PanbuExtraColors(
    progress = PanbuPalette.PurpleSoft,
    progressTrack = PanbuPalette.Track,
    success = PanbuPalette.SageStrong,
    positiveText = PanbuPalette.SageText,
    overLimit = PanbuPalette.RoseStrong,
)

private val DarkExtraColors = PanbuExtraColors(
    progress = PanbuPalette.NightPurple,
    progressTrack = PanbuPalette.NightLavender,
    success = PanbuPalette.Sage,
    positiveText = PanbuPalette.Sage,
    overLimit = PanbuPalette.NightRose,
)

private val LocalPanbuExtraColors = staticCompositionLocalOf { LightExtraColors }

/**
 * Tema de Panbu.
 *
 * Por ahora la app usa siempre el tema claro: el oscuro es un borrador y la opción
 * "Cambiar el tema" de Ajustes se implementa en la Fase 1.
 */
@Composable
fun PanbuTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalPanbuExtraColors provides if (darkTheme) DarkExtraColors else LightExtraColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = PanbuTypography,
            shapes = PanbuShapes,
            content = content,
        )
    }
}

/** Acceso a los valores del tema que no son de Material: `PanbuTheme.extraColors.progress`. */
object PanbuTheme {
    val extraColors: PanbuExtraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPanbuExtraColors.current
}

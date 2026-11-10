package com.panbu.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.panbu.app.ui.theme.PanbuSpacing

/**
 * Estructura común de las pantallas: cabecera con el logo, márgenes y scroll vertical.
 * Los insets del sistema (barra de estado y de navegación) los aplica el Scaffold de PanbuAppRoot.
 */
@Composable
fun PanbuScreen(
    modifier: Modifier = Modifier,
    showTagline: Boolean = false,
    onBack: (() -> Unit)? = null,
    headerActions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PanbuSpacing.screenHorizontal)
            .padding(bottom = PanbuSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PanbuSpacing.lg),
    ) {
        PanbuHeader(showTagline = showTagline, onBack = onBack, actions = headerActions)
        content()
    }
}

package com.panbu.app.feature.shop

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.panbu.app.R
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.UnderConstructionCard
import com.panbu.app.ui.theme.PanbuTheme

/** Tienda "Agregar compra" (docs/02-producto.md §5, ADR-0003). Se implementa en la Fase 3. */
@Composable
fun ShopScreen(onBack: () -> Unit) {
    PanbuScreen(onBack = onBack) {
        ScreenTitle(text = stringResource(R.string.shop_title), iconRes = R.drawable.ic_bag)
        UnderConstructionCard(phase = 3, description = stringResource(R.string.shop_placeholder))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF)
@Composable
private fun ShopScreenPreview() {
    PanbuTheme { ShopScreen(onBack = {}) }
}

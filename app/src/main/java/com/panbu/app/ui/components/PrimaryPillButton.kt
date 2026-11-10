package com.panbu.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Botón principal en forma de píldora, a todo el ancho ("Comenzar", "Necesito un momento de calma"…). */
@Composable
fun PrimaryPillButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

package com.panbu.app.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.panbu.app.R
import com.panbu.app.ui.theme.PanbuSpacing

/**
 * Número de la línea de ayuda. Es un dato de seguridad: se cambia solo aquí y en los textos `help_*` de
 * strings.xml, y antes de publicar se verifica que siga vigente (docs/06-guardrails.md, regla 3).
 */
object HelpLine {
    /** Línea 113 del Minsa (Perú), gratuita y las 24 horas. */
    const val PHONE = "113"
}

/**
 * "¿Necesitas hablar con alguien?" (guardrail 2): siempre hay una salida a ayuda humana.
 * El botón abre el marcador del teléfono con el número escrito; no llama solo ni necesita permisos.
 */
@Composable
fun HelpLineCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    PanbuCard(modifier = modifier.fillMaxWidth(), containerColor = MaterialTheme.colorScheme.secondaryContainer) {
        Text(
            text = stringResource(R.string.help_title),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(PanbuSpacing.xs))
        Text(text = stringResource(R.string.help_body), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(PanbuSpacing.md))
        OutlinedButton(
            onClick = {
                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${HelpLine.PHONE}"))
                // En una tableta sin teléfono no hay marcador: el número sigue a la vista en el texto.
                try {
                    context.startActivity(dial)
                } catch (_: ActivityNotFoundException) {
                }
            },
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = PanbuSpacing.minTouchTarget),
        ) {
            Text(text = stringResource(R.string.help_call), style = MaterialTheme.typography.labelLarge)
        }
    }
}

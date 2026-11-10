package com.panbu.app.ui.components

import android.text.format.DateFormat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.panbu.app.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** "22:00" o "10:00 p. m.", según el formato de hora que la persona usa en su celular. */
@Composable
fun formatTime(time: LocalTime): String {
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    val pattern = if (is24Hour) "HH:mm" else "h:mm a"
    return time.format(DateTimeFormatter.ofPattern(pattern, Locale.forLanguageTag("es")))
}

/** Diálogo para elegir una hora con el reloj de Material 3 (sigue el formato 12/24 h del celular). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    initial: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
        is24Hour = DateFormat.is24HourFormat(LocalContext.current),
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}

package com.panbu.app.feature.journal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.domain.model.JournalEntry
import com.panbu.app.domain.model.Mood
import com.panbu.app.ui.components.ChoiceChip
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.PrimaryPillButton
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.color
import com.panbu.app.ui.components.formatTime
import com.panbu.app.ui.components.labelRes
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Instant
import java.time.ZoneId

// =====================================================================================================
// Lista de entradas
// =====================================================================================================

/** Mi diario emocional (docs/02-producto.md §4): entradas privadas, cifradas y nunca analizadas. */
@Composable
fun JournalScreen(
    onBack: () -> Unit,
    onWrite: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    JournalContent(uiState, onBack, onWrite, onOpenEntry)
}

/** Sin estado: la usan JournalScreen, el @Preview y las capturas de pantalla. */
@Composable
internal fun JournalContent(
    uiState: JournalUiState,
    onBack: () -> Unit,
    onWrite: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    zone: ZoneId = ZoneId.systemDefault(),
) {
    PanbuScreen(onBack = onBack) {
        ScreenTitle(text = stringResource(R.string.journal_title), iconRes = R.drawable.ic_journal)
        Text(
            text = stringResource(R.string.journal_privacy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PrimaryPillButton(text = stringResource(R.string.journal_write), onClick = onWrite)

        val entries = uiState.entries ?: return@PanbuScreen
        if (entries.isEmpty()) {
            PanbuCard(modifier = Modifier.fillMaxWidth(), containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ill_panda_heart),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                    )
                    Text(
                        text = stringResource(R.string.journal_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            entries.forEach { entry -> JournalEntryCard(entry, zone, onClick = { onOpenEntry(entry.id) }) }
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry, zone: ZoneId, onClick: () -> Unit) {
    PanbuCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entryDate(entry.createdAt, zone),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            entry.mood?.let { MoodTag(it) }
        }
        Spacer(Modifier.height(PanbuSpacing.xs))
        Text(
            text = entry.text ?: stringResource(R.string.journal_unreadable),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = if (entry.text == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Punto de color + nombre del estado de ánimo. */
@Composable
private fun MoodTag(mood: Mood) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(mood.color()),
        )
        Spacer(Modifier.width(PanbuSpacing.xs))
        Text(text = stringResource(mood.labelRes()), style = MaterialTheme.typography.labelLarge)
    }
}

/** "Martes 10 de noviembre · 9:30 p. m." */
@Composable
private fun entryDate(instant: Instant, zone: ZoneId): String {
    val dateTime = instant.atZone(zone)
    val weekday = stringArrayResource(R.array.weekdays_full)[dateTime.dayOfWeek.value - 1]
    val month = stringArrayResource(R.array.months_full)[dateTime.monthValue - 1]
    return stringResource(
        R.string.journal_date,
        weekday.replaceFirstChar { it.uppercase() },
        dateTime.dayOfMonth,
        month,
        formatTime(dateTime.toLocalTime()),
    )
}

// =====================================================================================================
// Escribir / editar
// =====================================================================================================

@Composable
fun JournalEditorScreen(onClose: () -> Unit, viewModel: JournalEditorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.closed) { if (uiState.closed) onClose() }
    JournalEditorContent(
        uiState = uiState,
        onTextChange = viewModel::onTextChange,
        onMoodToggle = viewModel::onMoodToggle,
        onSave = viewModel::onSave,
        onDelete = viewModel::onDelete,
        onClose = onClose,
    )
}

private enum class EditorDialog { DISCARD, DELETE }

/** Sin estado: la usan JournalEditorScreen, el @Preview y las capturas de pantalla. */
@Composable
internal fun JournalEditorContent(
    uiState: JournalEditorUiState,
    onTextChange: (String) -> Unit,
    onMoodToggle: (Mood) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    zone: ZoneId = ZoneId.systemDefault(),
) {
    var dialog by rememberSaveable { mutableStateOf<EditorDialog?>(null) }
    // Salir con cambios sin guardar pregunta primero (flecha de la pantalla y botón atrás del celular).
    val requestClose = { if (uiState.hasChanges) dialog = EditorDialog.DISCARD else onClose() }
    BackHandler(enabled = uiState.hasChanges) { dialog = EditorDialog.DISCARD }

    PanbuScreen(onBack = requestClose) {
        ScreenTitle(
            text = stringResource(if (uiState.isNew) R.string.journal_new_title else R.string.journal_edit_title),
            iconRes = R.drawable.ic_journal,
        )
        if (uiState.loading) return@PanbuScreen
        uiState.createdAt?.let {
            Text(
                text = entryDate(it, zone),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (uiState.unreadable) {
            Text(text = stringResource(R.string.journal_unreadable), style = MaterialTheme.typography.bodyLarge)
        } else {
            Text(
                text = stringResource(R.string.journal_mood_question),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm),
            ) {
                Mood.entries.forEach { mood ->
                    ChoiceChip(
                        text = stringResource(mood.labelRes()),
                        selected = mood == uiState.mood,
                        onClick = { onMoodToggle(mood) },
                        dotColor = mood.color(),
                    )
                }
            }
            OutlinedTextField(
                value = uiState.text,
                onValueChange = onTextChange,
                placeholder = { Text(stringResource(R.string.journal_placeholder)) },
                minLines = 8,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
            )
            PrimaryPillButton(
                text = stringResource(R.string.save),
                onClick = onSave,
                enabled = uiState.canSave,
            )
        }
        if (!uiState.isNew) {
            TextButton(
                onClick = { dialog = EditorDialog.DELETE },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = PanbuSpacing.minTouchTarget),
            ) { Text(stringResource(R.string.journal_delete)) }
        }
    }

    when (dialog) {
        EditorDialog.DISCARD -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text(stringResource(R.string.journal_discard_title)) },
            text = { Text(stringResource(R.string.journal_discard_body)) },
            confirmButton = {
                TextButton(onClick = { dialog = null; onClose() }) {
                    Text(stringResource(R.string.journal_discard_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialog = null }) { Text(stringResource(R.string.journal_keep_writing)) }
            },
        )
        EditorDialog.DELETE -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text(stringResource(R.string.journal_delete_title)) },
            text = { Text(stringResource(R.string.journal_delete_body)) },
            confirmButton = {
                TextButton(onClick = { dialog = null; onDelete() }) {
                    Text(stringResource(R.string.journal_delete_confirm))
                }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text(stringResource(R.string.cancel)) } },
        )
        null -> Unit
    }
}

// --- Vistas previas (textos de ejemplo solo aquí) ---

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 900)
@Composable
private fun JournalEditorPreview() {
    PanbuTheme {
        JournalEditorContent(
            uiState = JournalEditorUiState(mood = Mood.CALM, text = "Hoy respiré con Panbu antes de dormir."),
            onTextChange = {}, onMoodToggle = {}, onSave = {}, onDelete = {}, onClose = {},
        )
    }
}

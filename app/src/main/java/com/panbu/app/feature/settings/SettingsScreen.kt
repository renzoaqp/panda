package com.panbu.app.feature.settings

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.BuildConfig
import com.panbu.app.R
import com.panbu.app.domain.model.RestReminder
import com.panbu.app.domain.model.UserPreferences
import com.panbu.app.reminders.canPostNotifications
import com.panbu.app.reminders.needsNotificationPermission
import com.panbu.app.ui.components.ChoiceChip
import com.panbu.app.ui.components.HelpLineCard
import com.panbu.app.ui.components.PanbuCard
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.SectionTitle
import com.panbu.app.ui.components.SettingsRow
import com.panbu.app.ui.components.SettingsSwitchRow
import com.panbu.app.ui.components.TimePickerDialog
import com.panbu.app.ui.components.formatTime
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme
import java.time.LocalTime

/** Ajustes (docs/02-producto.md §6). El permiso de notificaciones se pide aquí, al activar el recordatorio. */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val preferences by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Se vuelve a mirar cada vez que la persona regresa a la app (pudo cambiarlo en los ajustes del celular).
    var notificationsAllowed by remember { mutableStateOf(context.canPostNotifications()) }
    var permissionDenied by rememberSaveable { mutableStateOf(false) }
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }
    LifecycleResumeEffect(Unit) {
        notificationsAllowed = context.canPostNotifications()
        onPauseOrDispose {}
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notificationsAllowed = context.canPostNotifications()
        permissionDenied = !granted
        if (granted) viewModel.onRemindersChange(true)
    }

    SettingsContent(
        preferences = preferences ?: return,
        notificationsAllowed = notificationsAllowed,
        permissionDenied = permissionDenied,
        onNameChange = viewModel::onNameChange,
        onRemindersChange = { enabled ->
            when {
                !enabled -> viewModel.onRemindersChange(false)
                // Android 13+: primero se explica para qué es el permiso, y después se pide.
                context.needsNotificationPermission() -> showPermissionDialog = true
                else -> viewModel.onRemindersChange(true)
            }
        },
        onRestScheduleChange = viewModel::onRestScheduleChange,
        onReminderMinutesChange = viewModel::onReminderMinutesChange,
        onOpenSystemSettings = {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName),
            )
        },
        onEraseAll = viewModel::onEraseAll,
    )

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.settings_permission_title)) },
            text = { Text(stringResource(R.string.settings_permission_body)) },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text(stringResource(R.string.continue_action)) }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

/** Qué diálogo está abierto. Se guarda por nombre para sobrevivir a la rotación. */
private enum class SettingsDialog { NAME, REST_START, REST_END, ERASE }

/** Sin estado: la usan SettingsScreen, el @Preview y las capturas de pantalla. */
@Composable
internal fun SettingsContent(
    preferences: UserPreferences,
    notificationsAllowed: Boolean,
    permissionDenied: Boolean,
    onNameChange: (String) -> Unit,
    onRemindersChange: (Boolean) -> Unit,
    onRestScheduleChange: (LocalTime, LocalTime) -> Unit,
    onReminderMinutesChange: (Int) -> Unit,
    onOpenSystemSettings: () -> Unit,
    onEraseAll: () -> Unit,
) {
    var dialog by rememberSaveable { mutableStateOf<SettingsDialog?>(null) }
    // El inicio elegido mientras se elige el fin del horario de descanso.
    var pendingRestStart by rememberSaveable { mutableStateOf<LocalTime?>(null) }

    PanbuScreen {
        ScreenTitle(text = stringResource(R.string.settings_title), iconRes = R.drawable.ic_nav_settings)

        // --- Perfil ---
        SectionTitle(stringResource(R.string.settings_section_profile))
        PanbuCard(modifier = Modifier.fillMaxWidth()) {
            SettingsRow(
                title = stringResource(R.string.settings_name),
                value = preferences.displayName ?: stringResource(R.string.settings_name_empty),
                onClick = { dialog = SettingsDialog.NAME },
            )
        }

        // --- Descanso y recordatorio ---
        SectionTitle(stringResource(R.string.settings_section_rest))
        PanbuCard(modifier = Modifier.fillMaxWidth()) {
            SettingsRow(
                title = stringResource(R.string.settings_rest_schedule),
                value = stringResource(
                    R.string.settings_rest_schedule_value,
                    formatTime(preferences.restStart),
                    formatTime(preferences.restEnd),
                ),
                valueColor = MaterialTheme.colorScheme.primary,
                onClick = { dialog = SettingsDialog.REST_START },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsSwitchRow(
                title = stringResource(R.string.settings_reminders),
                subtitle = stringResource(R.string.settings_reminders_desc),
                checked = preferences.notificationsEnabled,
                onCheckedChange = onRemindersChange,
            )
            if (preferences.notificationsEnabled) {
                Text(
                    text = stringResource(R.string.settings_reminder_before),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(PanbuSpacing.sm))
                Row(
                    modifier = Modifier.selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm),
                ) {
                    UserPreferences.REMINDER_OPTIONS.forEach { minutes ->
                        ChoiceChip(
                            text = if (minutes == 60) {
                                stringResource(R.string.settings_reminder_hour)
                            } else {
                                stringResource(R.string.settings_reminder_minutes, minutes)
                            },
                            selected = minutes == preferences.reminderMinutesBefore,
                            onClick = { onReminderMinutesChange(minutes) },
                        )
                    }
                }
                Spacer(Modifier.height(PanbuSpacing.sm))
                Text(
                    text = stringResource(
                        R.string.settings_reminder_at,
                        formatTime(RestReminder.reminderTime(preferences.restStart, preferences.reminderMinutesBefore)),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // Si Android no deja mostrar notificaciones, se explica con calma cómo activarlas (sin alarmas rojas).
            if (!notificationsAllowed && (preferences.notificationsEnabled || permissionDenied)) {
                Spacer(Modifier.height(PanbuSpacing.sm))
                Text(
                    text = stringResource(R.string.settings_notifications_off),
                    style = MaterialTheme.typography.bodySmall,
                )
                TextButton(onClick = onOpenSystemSettings) {
                    Text(stringResource(R.string.settings_open_system_settings))
                }
            }
        }

        // --- Ayuda ---
        SectionTitle(stringResource(R.string.settings_section_help))
        HelpLineCard()

        // --- Privacidad ---
        SectionTitle(stringResource(R.string.settings_section_privacy))
        PanbuCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.settings_privacy_body), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(PanbuSpacing.sm))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                title = stringResource(R.string.settings_erase_all),
                onClick = { dialog = SettingsDialog.ERASE },
            )
        }

        // --- Lo que viene ---
        SectionTitle(stringResource(R.string.settings_section_soon))
        PanbuCard(modifier = Modifier.fillMaxWidth()) {
            listOf(
                R.string.settings_account,
                R.string.settings_sounds,
                R.string.settings_theme,
                R.string.settings_legal,
            ).forEachIndexed { index, title ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsRow(title = stringResource(title), value = stringResource(R.string.soon))
            }
        }

        Text(
            text = stringResource(R.string.disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    // --- Diálogos ---
    when (dialog) {
        SettingsDialog.NAME -> NameDialog(
            initial = preferences.displayName.orEmpty(),
            onSave = { onNameChange(it); dialog = null },
            onDismiss = { dialog = null },
        )
        SettingsDialog.REST_START -> TimePickerDialog(
            title = stringResource(R.string.settings_rest_start_title),
            initial = preferences.restStart,
            onConfirm = { pendingRestStart = it; dialog = SettingsDialog.REST_END },
            onDismiss = { dialog = null },
        )
        SettingsDialog.REST_END -> TimePickerDialog(
            title = stringResource(R.string.settings_rest_end_title),
            initial = preferences.restEnd,
            onConfirm = { end ->
                onRestScheduleChange(pendingRestStart ?: preferences.restStart, end)
                pendingRestStart = null
                dialog = null
            },
            onDismiss = { pendingRestStart = null; dialog = null },
        )
        SettingsDialog.ERASE -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text(stringResource(R.string.settings_erase_title)) },
            text = { Text(stringResource(R.string.settings_erase_body)) },
            confirmButton = {
                TextButton(onClick = { dialog = null; onEraseAll() }) {
                    Text(stringResource(R.string.settings_erase_confirm))
                }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text(stringResource(R.string.cancel)) } },
        )
        null -> Unit
    }
}

@Composable
private fun NameDialog(initial: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_name_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.settings_name_dialog_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = PanbuSpacing.sm),
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(UserPreferences.MAX_NAME_LENGTH) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = { TextButton(onClick = { onSave(name) }) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 1400)
@Composable
private fun SettingsContentPreview() {
    PanbuTheme {
        SettingsContent(
            preferences = UserPreferences(userName = "Ana", notificationsEnabled = true),
            notificationsAllowed = true,
            permissionDenied = false,
            onNameChange = {}, onRemindersChange = {}, onRestScheduleChange = { _, _ -> },
            onReminderMinutesChange = {}, onOpenSystemSettings = {}, onEraseAll = {},
        )
    }
}

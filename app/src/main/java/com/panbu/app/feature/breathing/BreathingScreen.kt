package com.panbu.app.feature.breathing

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.domain.model.BreathingPhase
import com.panbu.app.domain.model.BreathingStep
import com.panbu.app.ui.components.ChoiceChip
import com.panbu.app.ui.components.KeepScreenOn
import com.panbu.app.ui.components.PanbuProgressBar
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.SectionTitle
import com.panbu.app.ui.components.formatClock
import com.panbu.app.ui.components.formatDuration
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Duration

/** Respira con Panbu: el panda crece al inhalar y se achica al exhalar. */
@Composable
fun BreathingScreen(onClose: () -> Unit, viewModel: BreathingViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BreathingContent(
        state = state,
        onPresetSelected = viewModel::onPresetSelected,
        onMinutesSelected = viewModel::onMinutesSelected,
        onStart = viewModel::start,
        onFinish = viewModel::finish,
        onAgain = viewModel::again,
        onClose = onClose,
    )
}

@Composable
internal fun BreathingContent(
    state: BreathingUiState,
    onPresetSelected: (BreathingPreset) -> Unit,
    onMinutesSelected: (Int) -> Unit,
    onStart: () -> Unit,
    onFinish: () -> Unit,
    onAgain: () -> Unit,
    onClose: () -> Unit,
) {
    KeepScreenOn(enabled = state.stage == BreathingStage.RUNNING)
    PanbuScreen(onBack = onClose) {
        ScreenTitle(text = stringResource(R.string.breathing_title), iconRes = R.drawable.ic_breath)
        when (state.stage) {
            BreathingStage.SETUP -> BreathingSetup(state, onPresetSelected, onMinutesSelected, onStart)
            BreathingStage.RUNNING -> BreathingRunning(state, onFinish)
            BreathingStage.FINISHED -> CalmFinished(
                title = stringResource(R.string.breathing_done_title),
                text = if (state.finishedCounts) {
                    stringResource(R.string.breathing_done_text, formatDuration(state.finishedDuration))
                } else {
                    stringResource(R.string.calm_too_short)
                },
                onClose = onClose,
                onAgain = onAgain,
            )
        }
    }
}

@Composable
private fun ColumnScope.BreathingSetup(
    state: BreathingUiState,
    onPresetSelected: (BreathingPreset) -> Unit,
    onMinutesSelected: (Int) -> Unit,
    onStart: () -> Unit,
) {
    Text(text = stringResource(R.string.breathing_intro), style = MaterialTheme.typography.bodyLarge)
    SectionTitle(stringResource(R.string.breathing_pattern))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm),
    ) {
        BreathingPreset.entries.forEach { preset ->
            ChoiceChip(
                text = stringResource(preset.labelRes),
                selected = preset == state.preset,
                onClick = { onPresetSelected(preset) },
            )
        }
    }
    SectionTitle(stringResource(R.string.breathing_duration))
    Row(
        modifier = Modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm),
    ) {
        BreathingUiState.MINUTE_OPTIONS.forEach { minutes ->
            ChoiceChip(
                text = formatDuration(Duration.ofMinutes(minutes.toLong())),
                selected = minutes == state.minutes,
                onClick = { onMinutesSelected(minutes) },
            )
        }
    }
    Image(
        painter = painterResource(R.drawable.ill_panda_heart),
        contentDescription = null,
        modifier = Modifier
            .size(160.dp)
            .align(Alignment.CenterHorizontally),
    )
    PrimaryPillButton(text = stringResource(R.string.breathing_start), onClick = onStart)
}

@Composable
private fun ColumnScope.BreathingRunning(
    state: BreathingUiState,
    onFinish: () -> Unit,
) {
    val step = state.step ?: return
    BreathingPanda(step)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(step.phase.labelRes()),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        Text(text = step.secondsLeft.toString(), style = MaterialTheme.typography.displaySmall)
    }
    PanbuProgressBar(progress = state.progress)
    Text(
        text = stringResource(R.string.breathing_remaining, formatClock(state.remainingSeconds)),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    OutlinedButton(
        onClick = onFinish,
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
    ) {
        Text(text = stringResource(R.string.breathing_finish))
    }
}

/** Círculo lavanda y panda que crecen al inhalar y se achican al exhalar, al ritmo exacto de cada fase. */
@Composable
private fun BreathingPanda(step: BreathingStep) {
    val scale = remember { Animatable(SMALL) }
    LaunchedEffect(step.phase) {
        val target = when (step.phase) {
            BreathingPhase.INHALE, BreathingPhase.HOLD -> LARGE
            BreathingPhase.EXHALE, BreathingPhase.HOLD_EMPTY -> SMALL
        }
        val remainingMillis = ((1f - step.phaseProgress) * step.phaseSeconds * 1000).toInt().coerceAtLeast(1)
        scale.animateTo(target, tween(durationMillis = remainingMillis, easing = FastOutSlowInEasing))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(260.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
        Image(
            painter = painterResource(R.drawable.ill_panda_heart),
            contentDescription = null,
            modifier = Modifier
                .size(190.dp)
                .graphicsLayer {
                    val pandaScale = 0.86f + (scale.value - SMALL) / (LARGE - SMALL) * 0.14f
                    scaleX = pandaScale
                    scaleY = pandaScale
                },
        )
    }
}

private const val SMALL = 0.72f
private const val LARGE = 1f

private fun BreathingPhase.labelRes(): Int = when (this) {
    BreathingPhase.INHALE -> R.string.breathing_inhale
    BreathingPhase.HOLD, BreathingPhase.HOLD_EMPTY -> R.string.breathing_hold
    BreathingPhase.EXHALE -> R.string.breathing_exhale
}

/** Cierre de una actividad de calma (respiración o momento con Panbu). */
@Composable
internal fun ColumnScope.CalmFinished(
    title: String,
    text: String,
    onClose: () -> Unit,
    onAgain: (() -> Unit)? = null,
) {
    Image(
        painter = painterResource(R.drawable.ill_panda_heart),
        contentDescription = null,
        modifier = Modifier
            .size(180.dp)
            .align(Alignment.CenterHorizontally),
    )
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    PrimaryPillButton(text = stringResource(R.string.back_home), onClick = onClose)
    if (onAgain != null) {
        OutlinedButton(
            onClick = onAgain,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        ) {
            Text(text = stringResource(R.string.breathing_again))
        }
    }
}

@Composable
internal fun PrimaryPillButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 900)
@Composable
private fun BreathingRunningPreview() {
    PanbuTheme {
        BreathingContent(
            state = BreathingUiState(
                stage = BreathingStage.RUNNING,
                elapsedMillis = 21_000,
                step = BreathingPreset.GENTLE.pattern.stepAt(21_000),
            ),
            onPresetSelected = {}, onMinutesSelected = {}, onStart = {}, onFinish = {}, onAgain = {}, onClose = {},
        )
    }
}

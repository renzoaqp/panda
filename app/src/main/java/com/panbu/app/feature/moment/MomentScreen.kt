package com.panbu.app.feature.moment

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.feature.breathing.CalmFinished
import com.panbu.app.ui.components.PrimaryPillButton
import com.panbu.app.ui.components.KeepScreenOn
import com.panbu.app.ui.components.PanbuScreen
import com.panbu.app.ui.components.ScreenTitle
import com.panbu.app.ui.components.formatClock
import com.panbu.app.ui.components.formatDuration
import com.panbu.app.ui.theme.PanbuTheme

/** Un momento con Panbu (docs/02-producto.md §3): cuenta el "Tiempo contigo". */
@Composable
fun MomentScreen(onClose: () -> Unit, viewModel: MomentViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MomentContent(state = state, onStart = viewModel::start, onFinish = viewModel::finish, onClose = onClose)
}

@Composable
internal fun MomentContent(
    state: MomentUiState,
    onStart: () -> Unit,
    onFinish: () -> Unit,
    onClose: () -> Unit,
    /** false en las capturas de pantalla: una animación infinita nunca "termina" y el test se quedaría esperando. */
    animatePulse: Boolean = true,
) {
    KeepScreenOn(enabled = state.stage == MomentStage.RUNNING)
    PanbuScreen(onBack = onClose) {
        ScreenTitle(text = stringResource(R.string.moment_title), iconRes = R.drawable.ic_heart)
        when (state.stage) {
            MomentStage.IDLE -> {
                Text(text = stringResource(R.string.moment_intro), style = MaterialTheme.typography.bodyLarge)
                Image(
                    painter = painterResource(R.drawable.ill_panda_heart),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally),
                )
                PrimaryPillButton(text = stringResource(R.string.moment_start), onClick = onStart)
            }
            MomentStage.RUNNING -> {
                val pulse = if (animatePulse) {
                    val value by rememberInfiniteTransition(label = "momento").animateFloat(
                        initialValue = 0.96f,
                        targetValue = 1.04f,
                        animationSpec = infiniteRepeatable(tween(3_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                        label = "pulso",
                    )
                    value
                } else {
                    1f
                }
                Image(
                    painter = painterResource(R.drawable.ill_panda_heart),
                    contentDescription = null,
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.CenterHorizontally)
                        .graphicsLayer {
                            scaleX = pulse
                            scaleY = pulse
                        },
                )
                Text(
                    text = stringResource(R.string.moment_running),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = formatClock(state.elapsedSeconds),
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = onFinish,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                ) {
                    Text(text = stringResource(R.string.moment_finish))
                }
            }
            MomentStage.FINISHED -> CalmFinished(
                title = stringResource(R.string.moment_done_title),
                text = if (state.finishedCounts) {
                    stringResource(R.string.moment_done_text, formatDuration(state.finishedDuration))
                } else {
                    stringResource(R.string.calm_too_short)
                },
                onClose = onClose,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 900)
@Composable
private fun MomentRunningPreview() {
    PanbuTheme {
        MomentContent(
            state = MomentUiState(stage = MomentStage.RUNNING, elapsedSeconds = 754),
            onStart = {}, onFinish = {}, onClose = {},
        )
    }
}

package com.panbu.app.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.panbu.app.R
import com.panbu.app.ui.components.HelpLineCard
import com.panbu.app.ui.components.IconBubble
import com.panbu.app.ui.components.PrimaryPillButton
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme

/**
 * Bienvenida (docs/02-producto.md §7): qué es Panbu → cómo acompaña → privacidad → nombre → aviso de que
 * no sustituye a un profesional, con la línea de ayuda. No pide permisos ni cuenta.
 */
@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // El botón "atrás" del celular vuelve al paso anterior (en el primero, sale de la app como siempre).
    BackHandler(enabled = !uiState.isFirst, onBack = viewModel::onBack)
    OnboardingContent(
        uiState = uiState,
        onNext = viewModel::onNext,
        onBack = viewModel::onBack,
        onNameChange = viewModel::onNameChange,
        onFinish = viewModel::onFinish,
    )
}

/** Sin estado: la usan OnboardingScreen, el @Preview y las capturas de pantalla. */
@Composable
internal fun OnboardingContent(
    uiState: OnboardingUiState,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onFinish: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
            .padding(horizontal = PanbuSpacing.xl, vertical = PanbuSpacing.lg),
    ) {
        StepDots(current = uiState.step)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = PanbuSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PanbuSpacing.lg, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (uiState.step) {
                OnboardingStep.WELCOME -> WelcomeStep()
                OnboardingStep.HOW -> HowStep()
                OnboardingStep.PRIVACY -> PrivacyStep()
                OnboardingStep.NAME -> NameStep(uiState.name, onNameChange, onDone = onNext)
                OnboardingStep.DISCLAIMER -> DisclaimerStep()
            }
        }
        if (uiState.isLast) {
            PrimaryPillButton(text = stringResource(R.string.onboarding_start), onClick = onFinish)
        } else {
            PrimaryPillButton(text = stringResource(R.string.onboarding_next), onClick = onNext)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = PanbuSpacing.minTouchTarget),
            contentAlignment = Alignment.Center,
        ) {
            if (!uiState.isFirst) {
                TextButton(onClick = onBack) { Text(stringResource(R.string.onboarding_back)) }
            }
        }
    }
}

// --- Pasos ---

@Composable
private fun WelcomeStep() {
    Image(
        painter = painterResource(R.drawable.ill_panda_heart),
        contentDescription = null,
        modifier = Modifier.size(200.dp),
    )
    StepTitle(R.string.onboarding_welcome_title)
    StepBody(R.string.onboarding_welcome_body)
}

@Composable
private fun HowStep() {
    StepTitle(R.string.onboarding_how_title)
    Column(verticalArrangement = Arrangement.spacedBy(PanbuSpacing.md)) {
        IconLine(R.drawable.ic_breath, R.string.onboarding_how_breathe)
        IconLine(R.drawable.ic_heart, R.string.onboarding_how_mood)
        IconLine(R.drawable.ic_nav_goals, R.string.onboarding_how_goals)
        IconLine(R.drawable.ic_moon, R.string.onboarding_how_rest)
    }
}

@Composable
private fun PrivacyStep() {
    StepTitle(R.string.onboarding_privacy_title)
    Column(verticalArrangement = Arrangement.spacedBy(PanbuSpacing.md)) {
        IconLine(R.drawable.ic_check, R.string.onboarding_privacy_local)
        IconLine(R.drawable.ic_check, R.string.onboarding_privacy_no_ads)
        IconLine(R.drawable.ic_check, R.string.onboarding_privacy_erase)
    }
}

@Composable
private fun NameStep(name: String, onNameChange: (String) -> Unit, onDone: () -> Unit) {
    StepTitle(R.string.onboarding_name_title)
    StepBody(R.string.onboarding_name_body)
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.onboarding_name_label)) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DisclaimerStep() {
    Image(
        painter = painterResource(R.drawable.ill_panda_heart),
        contentDescription = null,
        modifier = Modifier.size(120.dp),
    )
    StepTitle(R.string.onboarding_disclaimer_title)
    StepBody(R.string.disclaimer)
    HelpLineCard()
}

// --- Piezas ---

@Composable
private fun StepTitle(@StringRes text: Int) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { heading() },
    )
}

@Composable
private fun StepBody(@StringRes text: Int) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun IconLine(@DrawableRes icon: Int, @StringRes text: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconBubble(icon)
        Spacer(Modifier.width(PanbuSpacing.md))
        Text(text = stringResource(text), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    }
}

/** Puntos de avance: el paso actual es una píldora morada. TalkBack lo lee como "Paso 2 de 5". */
@Composable
private fun StepDots(current: OnboardingStep) {
    val description = stringResource(R.string.onboarding_step_cd, current.ordinal + 1, OnboardingStep.entries.size)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(PanbuSpacing.sm, Alignment.CenterHorizontally),
    ) {
        OnboardingStep.entries.forEach { step ->
            val active = step == current
            Box(
                Modifier
                    .height(8.dp)
                    .width(if (active) 24.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F5EF, heightDp = 800)
@Composable
private fun OnboardingPreview() {
    PanbuTheme {
        OnboardingContent(OnboardingUiState(step = OnboardingStep.HOW), {}, {}, {}, {})
    }
}

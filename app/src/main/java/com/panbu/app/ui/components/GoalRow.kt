package com.panbu.app.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.panbu.app.R
import com.panbu.app.domain.model.Goal
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.model.GoalProgress
import com.panbu.app.domain.model.GoalType
import com.panbu.app.domain.model.GoalUnit
import com.panbu.app.ui.theme.PanbuSpacing
import com.panbu.app.ui.theme.PanbuTheme
import java.time.Duration

/** Meta con ícono, valor, barra de progreso y porcentaje (mockup: pantallas Inicio y Metas). */
@Composable
fun GoalRow(item: GoalItem, modifier: Modifier = Modifier) {
    val progress = item.progress
    val achieved = progress?.isAchieved == true
    PanbuCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(item.goal.type.iconRes())
            Spacer(Modifier.width(PanbuSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(text = item.goal.displayTitle(), style = MaterialTheme.typography.titleSmall)
                Text(
                    text = progress?.valueText() ?: stringResource(R.string.goal_no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (progress != null) {
                    Spacer(Modifier.height(PanbuSpacing.sm))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PanbuProgressBar(
                            progress = progress.fraction,
                            color = progressColor(progress),
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(PanbuSpacing.sm))
                        Text(
                            text = stringResource(R.string.goal_percent, progress.percent),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            if (achieved) {
                Spacer(Modifier.width(PanbuSpacing.sm))
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = stringResource(R.string.goal_achieved),
                    tint = PanbuTheme.extraColors.success,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun progressColor(progress: GoalProgress) = when {
    progress.isLimit && !progress.isAchieved -> PanbuTheme.extraColors.overLimit
    progress.isAchieved -> PanbuTheme.extraColors.success
    else -> PanbuTheme.extraColors.progress
}

@Composable
private fun GoalProgress.valueText(): String = when (goal.unit) {
    GoalUnit.TIMES -> stringResource(R.string.goal_times, current, goal.target)
    GoalUnit.MINUTES -> stringResource(
        R.string.goal_value_pair,
        formatDuration(Duration.ofMinutes(current.toLong())),
        formatDuration(Duration.ofMinutes(goal.target.toLong())),
    )
}

/** Las metas predefinidas muestran su nombre traducido; las personalizadas, el que escribió la persona. */
@Composable
fun Goal.displayTitle(): String = type.titleRes()?.let { stringResource(it) } ?: title

@StringRes
private fun GoalType.titleRes(): Int? = when (this) {
    GoalType.CALM_MOMENTS -> R.string.goal_calm_moments
    GoalType.MINDFULNESS -> R.string.goal_mindfulness
    GoalType.NIGHT_SCREEN_LIMIT -> R.string.goal_night_screen
    GoalType.SLEEP -> R.string.goal_sleep
    GoalType.CUSTOM -> null
}

@DrawableRes
private fun GoalType.iconRes(): Int = when (this) {
    GoalType.CALM_MOMENTS -> R.drawable.ic_heart
    GoalType.MINDFULNESS -> R.drawable.ic_breath
    GoalType.NIGHT_SCREEN_LIMIT, GoalType.SLEEP, GoalType.CUSTOM -> R.drawable.ic_nav_goals
}

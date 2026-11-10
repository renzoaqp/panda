package com.panbu.app.ui.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.panbu.app.R
import com.panbu.app.domain.model.Mood
import com.panbu.app.ui.theme.MoodPalette

/** Texto visible de cada estado de ánimo. */
@StringRes
fun Mood.labelRes(): Int = when (this) {
    Mood.CALM -> R.string.mood_calm
    Mood.ANXIOUS -> R.string.mood_anxious
    Mood.SAD -> R.string.mood_sad
    Mood.STRESSED -> R.string.mood_stressed
    Mood.HAPPY -> R.string.mood_happy
}

/** Color de cada estado de ánimo en chips y gráficos. */
fun Mood.color(): Color = when (this) {
    Mood.CALM -> MoodPalette.Calm
    Mood.ANXIOUS -> MoodPalette.Anxious
    Mood.SAD -> MoodPalette.Sad
    Mood.STRESSED -> MoodPalette.Stressed
    Mood.HAPPY -> MoodPalette.Happy
}

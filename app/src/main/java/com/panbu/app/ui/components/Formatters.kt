package com.panbu.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.panbu.app.R
import java.time.Duration

/** "2 h 15 min", "45 min" o, si dura menos de un minuto, "40 s". */
@Composable
fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.seconds.coerceAtLeast(0)
    val totalMinutes = totalSeconds / 60
    return when {
        totalMinutes >= 60 -> stringResource(R.string.duration_h_min, totalMinutes / 60, totalMinutes % 60)
        totalMinutes == 0L && totalSeconds > 0 -> stringResource(R.string.duration_sec, totalSeconds)
        else -> stringResource(R.string.duration_min, totalMinutes)
    }
}

/** "4:05", para temporizadores. */
@Composable
fun formatClock(totalSeconds: Long): String {
    val seconds = totalSeconds.coerceAtLeast(0)
    return stringResource(R.string.duration_clock, seconds / 60, seconds % 60)
}

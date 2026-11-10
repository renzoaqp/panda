package com.panbu.app.domain.usecase

import com.panbu.app.data.repository.CalmSessionRepository
import com.panbu.app.data.repository.GoalRepository
import com.panbu.app.domain.model.GoalItem
import com.panbu.app.domain.model.progressFrom
import com.panbu.app.domain.model.totalDuration
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** El día de hoy en números: tiempo de calma (hoy y ayer) y el avance de cada meta activa. */
data class TodayProgress(
    val calmToday: Duration = Duration.ZERO,
    val calmYesterday: Duration = Duration.ZERO,
    val goals: List<GoalItem> = emptyList(),
)

/** Lo usan Inicio y Metas. "Hoy" es de medianoche a medianoche en la zona del celular. */
class ObserveTodayProgress @Inject constructor(
    private val calmSessions: CalmSessionRepository,
    private val goals: GoalRepository,
    private val clock: Clock,
) {
    operator fun invoke(): Flow<TodayProgress> {
        val zone = clock.zone
        val today = LocalDate.now(clock)
        val yesterdayStart = today.minusDays(1).atStartOfDay(zone).toInstant()
        val todayStart = today.atStartOfDay(zone).toInstant()
        val tomorrowStart = today.plusDays(1).atStartOfDay(zone).toInstant()

        return combine(
            calmSessions.observeStartedBetween(yesterdayStart, tomorrowStart),
            goals.observeActive(),
        ) { sessions, activeGoals ->
            val (todaySessions, yesterdaySessions) = sessions.partition { it.startedAt >= todayStart }
            TodayProgress(
                calmToday = todaySessions.totalDuration(),
                calmYesterday = yesterdaySessions.totalDuration(),
                goals = activeGoals.map { GoalItem(it, it.progressFrom(todaySessions)) },
            )
        }
    }
}

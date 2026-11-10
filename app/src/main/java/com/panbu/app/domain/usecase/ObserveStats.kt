package com.panbu.app.domain.usecase

import com.panbu.app.data.repository.CalmSessionRepository
import com.panbu.app.data.repository.MoodRepository
import com.panbu.app.domain.model.PeriodStats
import com.panbu.app.domain.model.StatsCalculator
import com.panbu.app.domain.model.StatsPeriod
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Estadísticas de un periodo (Semana, Mes o Total) con datos reales de la base.
 * Lee el periodo actual y el anterior (para la comparación) y aplica las reglas de [StatsCalculator].
 */
class ObserveStats @Inject constructor(
    private val calmSessions: CalmSessionRepository,
    private val moods: MoodRepository,
    private val clock: Clock,
) {
    operator fun invoke(period: StatsPeriod): Flow<PeriodStats> {
        val zone = clock.zone
        val today = LocalDate.now(clock)
        val range = StatsCalculator.queryRange(period, today)
        // En Total se lee desde el principio hasta el final de hoy.
        val from = range?.start?.atStartOfDay(zone)?.toInstant() ?: Instant.EPOCH
        val to = (range?.endExclusive ?: today.plusDays(1)).atStartOfDay(zone).toInstant()

        return combine(
            calmSessions.observeStartedBetween(from, to),
            moods.observeBetween(from, to),
        ) { sessions, moodEntries ->
            StatsCalculator.build(period, today, zone, sessions, moodEntries)
        }
    }
}

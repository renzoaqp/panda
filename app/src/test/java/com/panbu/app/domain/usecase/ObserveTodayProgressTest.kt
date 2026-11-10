package com.panbu.app.domain.usecase

import com.panbu.app.data.repository.CalmSessionRepository
import com.panbu.app.data.repository.GoalRepository
import com.panbu.app.domain.model.CalmActivityType
import com.panbu.app.domain.model.CalmSession
import com.panbu.app.domain.model.DataSource
import com.panbu.app.domain.model.DefaultGoals
import com.panbu.app.domain.model.Goal
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** "Hoy" y "ayer" en la zona del celular (Lima, UTC-5), con un reloj fijo. */
class ObserveTodayProgressTest {

    private val lima = ZoneId.of("America/Lima")

    // 10 de noviembre de 2026, 10:00 en Lima.
    private val clock = Clock.fixed(Instant.parse("2026-11-10T15:00:00Z"), lima)

    private fun at(localDateTime: String, minutes: Long, type: CalmActivityType = CalmActivityType.BREATHING): CalmSession {
        val start = java.time.LocalDateTime.parse(localDateTime).atZone(lima).toInstant()
        return CalmSession(
            type = type,
            source = DataSource.MANUAL,
            startedAt = start,
            endedAt = start.plus(Duration.ofMinutes(minutes)),
        )
    }

    private class FakeCalmSessions(sessions: List<CalmSession>) : CalmSessionRepository {
        val all = MutableStateFlow(sessions)
        override fun observeStartedBetween(from: Instant, to: Instant): Flow<List<CalmSession>> =
            all.map { list -> list.filter { it.startedAt >= from && it.startedAt < to } }

        override suspend fun record(type: CalmActivityType, startedAt: Instant, endedAt: Instant, source: DataSource) =
            error("No se usa en este test")
    }

    private class FakeGoals(private val goals: List<Goal>) : GoalRepository {
        override fun observeActive(): Flow<List<Goal>> = flowOf(goals)
    }

    @Test
    fun `splits calm time between today and yesterday using the local day`() = runTest {
        val sessions = listOf(
            at("2026-11-09T23:50:00", minutes = 20), // empieza ayer (aunque termina hoy): cuenta para ayer
            at("2026-11-10T00:05:00", minutes = 10), // hoy, apenas pasada la medianoche
            at("2026-11-10T09:00:00", minutes = 30, type = CalmActivityType.MOMENT_WITH_PANBU),
            at("2026-11-08T12:00:00", minutes = 60), // anteayer: no aparece
        )
        val useCase = ObserveTodayProgress(FakeCalmSessions(sessions), FakeGoals(DefaultGoals.all), clock)

        val today = useCase().first()

        assertEquals(Duration.ofMinutes(40), today.calmToday)
        assertEquals(Duration.ofMinutes(20), today.calmYesterday)
        // Momentos de calma: 2 de hoy (respiración + momento con Panbu). Respiración: solo 1.
        assertEquals(listOf(2, 1), today.goals.map { it.progress?.current })
    }

    @Test
    fun `an empty day has zero calm time and zero progress`() = runTest {
        val useCase = ObserveTodayProgress(FakeCalmSessions(emptyList()), FakeGoals(DefaultGoals.all), clock)

        val today = useCase().first()

        assertEquals(Duration.ZERO, today.calmToday)
        assertEquals(Duration.ZERO, today.calmYesterday)
        assertEquals(listOf(0, 0), today.goals.map { it.progress?.current })
    }
}

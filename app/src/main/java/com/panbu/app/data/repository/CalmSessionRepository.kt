package com.panbu.app.data.repository

import com.panbu.app.data.local.CalmSessionDao
import com.panbu.app.data.local.toDomain
import com.panbu.app.data.local.toEntity
import com.panbu.app.domain.model.CalmActivityType
import com.panbu.app.domain.model.CalmSession
import com.panbu.app.domain.model.DataSource
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Sesiones de calma: respiración, momentos con Panbu, etc. Son la base del "Tiempo contigo" y de las metas. */
interface CalmSessionRepository {
    /** Sesiones con `from <= startedAt < to`, de la más antigua a la más reciente. */
    fun observeStartedBetween(from: Instant, to: Instant): Flow<List<CalmSession>>

    /**
     * Guarda una sesión. Las que duran menos de 30 s no se guardan (no cuentan y solo serían ruido).
     * @return true si se guardó.
     */
    suspend fun record(
        type: CalmActivityType,
        startedAt: Instant,
        endedAt: Instant,
        source: DataSource = DataSource.MANUAL,
    ): Boolean
}

class RoomCalmSessionRepository @Inject constructor(
    private val dao: CalmSessionDao,
) : CalmSessionRepository {

    override fun observeStartedBetween(from: Instant, to: Instant): Flow<List<CalmSession>> =
        dao.observeStartedBetween(from.toEpochMilli(), to.toEpochMilli()).map { list -> list.map { it.toDomain() } }

    override suspend fun record(
        type: CalmActivityType,
        startedAt: Instant,
        endedAt: Instant,
        source: DataSource,
    ): Boolean {
        val session = CalmSession(type = type, source = source, startedAt = startedAt, endedAt = endedAt)
        if (!session.counts) return false
        dao.insert(session.toEntity())
        return true
    }
}

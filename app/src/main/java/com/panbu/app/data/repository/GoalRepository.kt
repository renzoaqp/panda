package com.panbu.app.data.repository

import com.panbu.app.data.local.GoalDao
import com.panbu.app.data.local.toDomain
import com.panbu.app.data.local.toEntity
import com.panbu.app.domain.model.DefaultGoals
import com.panbu.app.domain.model.Goal
import java.time.Clock
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface GoalRepository {
    /** Metas activas. La primera vez crea las metas iniciales (DefaultGoals). */
    fun observeActive(): Flow<List<Goal>>
}

class RoomGoalRepository @Inject constructor(
    private val dao: GoalDao,
    private val clock: Clock,
) : GoalRepository {

    // Inicio y Metas pueden pedir las metas al mismo tiempo: el candado evita crearlas dos veces.
    private val seedLock = Mutex()

    override fun observeActive(): Flow<List<Goal>> = flow {
        ensureDefaultGoals()
        emitAll(dao.observeActive().map { list -> list.map { it.toDomain() } })
    }

    private suspend fun ensureDefaultGoals() = seedLock.withLock {
        if (dao.countAll() == 0) {
            val now = clock.instant()
            DefaultGoals.all.forEach { dao.upsert(it.toEntity(createdAt = now)) }
        }
    }
}

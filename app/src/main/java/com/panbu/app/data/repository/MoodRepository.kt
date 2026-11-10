package com.panbu.app.data.repository

import com.panbu.app.data.local.MoodDao
import com.panbu.app.data.local.toDomain
import com.panbu.app.data.local.toEntity
import com.panbu.app.domain.model.Mood
import com.panbu.app.domain.model.MoodEntry
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Registros de "¿Cómo te sientes?". Ejemplo de referencia del patrón repositorio del proyecto. */
interface MoodRepository {
    /** El último registro, sea del día que sea (o null si no hay ninguno). */
    fun observeLatest(): Flow<MoodEntry?>

    /** Registros con `from <= createdAt < to`, del más reciente al más antiguo. */
    fun observeBetween(from: Instant, to: Instant): Flow<List<MoodEntry>>

    suspend fun record(mood: Mood, note: String? = null)
}

class RoomMoodRepository @Inject constructor(
    private val dao: MoodDao,
    private val clock: Clock,
) : MoodRepository {

    override fun observeLatest(): Flow<MoodEntry?> =
        dao.observeLatest().map { it?.toDomain() }

    override fun observeBetween(from: Instant, to: Instant): Flow<List<MoodEntry>> =
        dao.observeBetween(from.toEpochMilli(), to.toEpochMilli()).map { list -> list.map { it.toDomain() } }

    override suspend fun record(mood: Mood, note: String?) {
        dao.insert(MoodEntry(mood = mood, note = note, createdAt = clock.instant()).toEntity())
    }
}

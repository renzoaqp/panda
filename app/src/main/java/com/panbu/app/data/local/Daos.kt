package com.panbu.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Insert
    suspend fun insert(entry: MoodEntryEntity): Long

    @Query("SELECT * FROM mood_entries ORDER BY created_at DESC LIMIT 1")
    fun observeLatest(): Flow<MoodEntryEntity?>

    @Query(
        "SELECT * FROM mood_entries WHERE created_at >= :fromMillis AND created_at < :toMillis " +
            "ORDER BY created_at DESC",
    )
    fun observeBetween(fromMillis: Long, toMillis: Long): Flow<List<MoodEntryEntity>>
}

@Dao
interface CalmSessionDao {
    @Insert
    suspend fun insert(session: CalmSessionEntity): Long

    @Query(
        "SELECT * FROM calm_sessions WHERE started_at >= :fromMillis AND started_at < :toMillis " +
            "ORDER BY started_at ASC",
    )
    fun observeStartedBetween(fromMillis: Long, toMillis: Long): Flow<List<CalmSessionEntity>>
}

@Dao
interface GoalDao {
    @Upsert
    suspend fun upsert(goal: GoalEntity): Long

    @Query("SELECT * FROM goals WHERE is_active = 1 ORDER BY created_at ASC")
    fun observeActive(): Flow<List<GoalEntity>>
}

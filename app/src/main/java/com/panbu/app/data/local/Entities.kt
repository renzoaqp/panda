package com.panbu.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.panbu.app.domain.model.CalmActivityType
import com.panbu.app.domain.model.CalmSession
import com.panbu.app.domain.model.DataSource
import com.panbu.app.domain.model.Goal
import com.panbu.app.domain.model.GoalPeriod
import com.panbu.app.domain.model.GoalType
import com.panbu.app.domain.model.GoalUnit
import com.panbu.app.domain.model.Mood
import com.panbu.app.domain.model.MoodEntry
import java.time.Instant

// Tablas de Room. Documentadas en docs/05-modelo-de-datos.md.
// Los enums se guardan por `name` y las fechas como epoch millis.

@Entity(tableName = "mood_entries", indices = [Index("created_at")])
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mood: String,
    val note: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)

fun MoodEntryEntity.toDomain() = MoodEntry(
    id = id,
    mood = Mood.valueOf(mood),
    note = note,
    createdAt = Instant.ofEpochMilli(createdAt),
)

fun MoodEntry.toEntity() = MoodEntryEntity(
    id = id,
    mood = mood.name,
    note = note,
    createdAt = createdAt.toEpochMilli(),
)

@Entity(tableName = "calm_sessions", indices = [Index("started_at")])
data class CalmSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val source: String,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "ended_at") val endedAt: Long,
)

fun CalmSessionEntity.toDomain() = CalmSession(
    id = id,
    type = CalmActivityType.valueOf(type),
    source = DataSource.valueOf(source),
    startedAt = Instant.ofEpochMilli(startedAt),
    endedAt = Instant.ofEpochMilli(endedAt),
)

fun CalmSession.toEntity() = CalmSessionEntity(
    id = id,
    type = type.name,
    source = source.name,
    startedAt = startedAt.toEpochMilli(),
    endedAt = endedAt.toEpochMilli(),
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val target: Int,
    val unit: String,
    val period: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)

fun GoalEntity.toDomain() = Goal(
    id = id,
    type = GoalType.valueOf(type),
    title = title,
    target = target,
    unit = GoalUnit.valueOf(unit),
    period = GoalPeriod.valueOf(period),
    isActive = isActive,
)

fun Goal.toEntity(createdAt: Instant) = GoalEntity(
    id = id,
    type = type.name,
    title = title,
    target = target,
    unit = unit.name,
    period = period.name,
    isActive = isActive,
    createdAt = createdAt.toEpochMilli(),
)

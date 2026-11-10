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

/**
 * Entrada del diario. El texto se guarda **cifrado** (AES-GCM, clave en Android Keystore; ADR-0006) junto con
 * su IV. Nunca se guarda el texto plano ni se registra en logs.
 */
@Entity(tableName = "journal_entries", indices = [Index("created_at")])
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    /** Estado de ánimo opcional, por `name` de [Mood]. */
    val mood: String?,
    @ColumnInfo(name = "text_encrypted", typeAffinity = ColumnInfo.BLOB) val textEncrypted: ByteArray,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val iv: ByteArray,
) {
    // Una data class compara los ByteArray por referencia; aquí se comparan por contenido.
    override fun equals(other: Any?): Boolean = other is JournalEntryEntity &&
        other.id == id && other.createdAt == createdAt && other.updatedAt == updatedAt && other.mood == mood &&
        other.textEncrypted.contentEquals(textEncrypted) && other.iv.contentEquals(iv)

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + textEncrypted.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        return result
    }
}

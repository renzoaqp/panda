package com.panbu.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos local de Panbu (ADR-0002: los datos se quedan en el celular).
 *
 * Antes de la primera publicación se puede modificar la versión 1. Después, cada cambio de esquema
 * sube `version` y lleva una Migration con su test (docs/05-modelo-de-datos.md, "Migraciones").
 */
@Database(
    entities = [MoodEntryEntity::class, CalmSessionEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class PanbuDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun calmSessionDao(): CalmSessionDao
    abstract fun goalDao(): GoalDao

    companion object {
        const val NAME = "panbu.db"
    }
}

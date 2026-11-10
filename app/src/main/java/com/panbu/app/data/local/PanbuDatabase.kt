package com.panbu.app.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos local de Panbu (ADR-0002: los datos se quedan en el celular).
 *
 * Cada cambio de esquema sube `version` y lleva una migración (docs/05-modelo-de-datos.md, "Migraciones").
 * Ya hay APK de prueba instalados con datos reales, así que no se edita una versión anterior.
 *
 * Versiones: 1 = ánimo, sesiones de calma y metas (APK 0.1–0.2) · 2 = diario (APK 0.3).
 */
@Database(
    entities = [MoodEntryEntity::class, CalmSessionEntity::class, GoalEntity::class, JournalEntryEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        // v2 (APK 0.3): tabla nueva `journal_entries`. Room genera la migración comparando los esquemas
        // exportados en app/schemas/; los datos de las demás tablas se conservan.
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class PanbuDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun calmSessionDao(): CalmSessionDao
    abstract fun goalDao(): GoalDao
    abstract fun journalDao(): JournalDao

    companion object {
        const val NAME = "panbu.db"
    }
}

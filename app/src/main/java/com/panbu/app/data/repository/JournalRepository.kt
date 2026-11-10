package com.panbu.app.data.repository

import com.panbu.app.data.crypto.EncryptedText
import com.panbu.app.data.crypto.TextCipher
import com.panbu.app.data.local.JournalDao
import com.panbu.app.data.local.JournalEntryEntity
import com.panbu.app.domain.model.JournalEntry
import com.panbu.app.domain.model.Mood
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * "Mi diario emocional". El texto se cifra antes de llegar a la base de datos y se descifra al leerlo
 * (ADR-0006). El diario nunca se analiza ni sale del celular.
 */
interface JournalRepository {
    /** Todas las entradas, de la más reciente a la más antigua, ya descifradas. */
    fun observeAll(): Flow<List<JournalEntry>>

    suspend fun get(id: Long): JournalEntry?

    /** Crea una entrada (`id = 0`) o actualiza una existente. Devuelve su id. */
    suspend fun save(id: Long, mood: Mood?, text: String): Long

    suspend fun delete(id: Long)
}

class RoomJournalRepository @Inject constructor(
    private val dao: JournalDao,
    private val cipher: TextCipher,
    private val clock: Clock,
) : JournalRepository {

    override fun observeAll(): Flow<List<JournalEntry>> =
        dao.observeAll()
            .map { entries -> entries.map { it.toDomain() } }
            .flowOn(Dispatchers.Default) // descifrar no debe trabar la pantalla

    override suspend fun get(id: Long): JournalEntry? = withContext(Dispatchers.Default) {
        dao.getById(id)?.toDomain()
    }

    override suspend fun save(id: Long, mood: Mood?, text: String): Long = withContext(Dispatchers.Default) {
        val now = clock.instant().toEpochMilli()
        val encrypted = cipher.encrypt(text.take(JournalEntry.MAX_LENGTH))
        val existing = if (id != 0L) dao.getById(id) else null
        if (existing == null) {
            dao.insert(
                JournalEntryEntity(
                    createdAt = now,
                    updatedAt = now,
                    mood = mood?.name,
                    textEncrypted = encrypted.cipherText,
                    iv = encrypted.iv,
                ),
            )
        } else {
            dao.update(
                existing.copy(
                    updatedAt = now,
                    mood = mood?.name,
                    textEncrypted = encrypted.cipherText,
                    iv = encrypted.iv,
                ),
            )
            existing.id
        }
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    private fun JournalEntryEntity.toDomain() = JournalEntry(
        id = id,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        mood = Mood.entries.firstOrNull { it.name == mood },
        // Si la clave ya no existe o los datos están dañados, la entrada queda "ilegible" (null), sin cerrar la app.
        text = try {
            cipher.decrypt(EncryptedText(textEncrypted, iv))
        } catch (_: Exception) {
            null
        },
    )
}

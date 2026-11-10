package com.panbu.app.data.repository

import com.panbu.app.data.crypto.EncryptedText
import com.panbu.app.data.crypto.TextCipher
import com.panbu.app.data.local.JournalDao
import com.panbu.app.data.local.JournalEntryEntity
import com.panbu.app.domain.model.Mood
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * El diario se guarda cifrado y se lee descifrado. El cifrado real (Android Keystore) solo existe en el
 * celular; aquí se usa uno de prueba que invierte los bytes, suficiente para ver qué llega a la base.
 */
class RoomJournalRepositoryTest {

    private class ReversingCipher : TextCipher {
        var keyDestroyed = false
        override fun encrypt(plainText: String) =
            EncryptedText(plainText.toByteArray().reversedArray(), iv = byteArrayOf(1, 2, 3))

        override fun decrypt(encrypted: EncryptedText): String {
            check(!keyDestroyed) { "Sin clave" }
            return String(encrypted.cipherText.reversedArray())
        }

        override fun destroyKey() {
            keyDestroyed = true
        }
    }

    private class FakeJournalDao : JournalDao {
        val rows = MutableStateFlow<List<JournalEntryEntity>>(emptyList())
        private var nextId = 1L

        override fun observeAll(): Flow<List<JournalEntryEntity>> = rows

        override suspend fun getById(id: Long) = rows.value.firstOrNull { it.id == id }

        override suspend fun insert(entry: JournalEntryEntity): Long {
            val id = nextId++
            rows.value = listOf(entry.copy(id = id)) + rows.value
            return id
        }

        override suspend fun update(entry: JournalEntryEntity) {
            rows.value = rows.value.map { if (it.id == entry.id) entry else it }
        }

        override suspend fun deleteById(id: Long) {
            rows.value = rows.value.filterNot { it.id == id }
        }
    }

    private val dao = FakeJournalDao()
    private val cipher = ReversingCipher()
    private var now = Instant.parse("2026-11-10T02:00:00Z")
    private val clock = object : Clock() {
        override fun getZone() = ZoneOffset.UTC
        override fun withZone(zone: java.time.ZoneId?) = this
        override fun instant() = now
    }
    private val repository = RoomJournalRepository(dao, cipher, clock)

    @Test
    fun `the text reaches the database encrypted and comes back readable`() = runTest {
        val id = repository.save(id = 0, mood = Mood.CALM, text = "Hoy respiré con Panbu")

        val stored = dao.rows.value.single()
        assertFalse(String(stored.textEncrypted).contains("Panbu")) // nunca texto plano en la base
        assertArrayEquals(byteArrayOf(1, 2, 3), stored.iv)

        val entry = repository.observeAll().first().single()
        assertEquals(id, entry.id)
        assertEquals("Hoy respiré con Panbu", entry.text)
        assertEquals(Mood.CALM, entry.mood)
    }

    @Test
    fun `editing keeps the creation date and changes the update date`() = runTest {
        val id = repository.save(id = 0, mood = null, text = "Primera versión")
        val created = now
        now = now.plusSeconds(3_600)

        repository.save(id = id, mood = Mood.HAPPY, text = "Segunda versión")

        val entry = repository.get(id)!!
        assertEquals(created, entry.createdAt)
        assertEquals(now, entry.updatedAt)
        assertEquals("Segunda versión", entry.text)
        assertEquals(1, dao.rows.value.size)
    }

    @Test
    fun `an entry that can no longer be decrypted is shown as unreadable instead of crashing`() = runTest {
        repository.save(id = 0, mood = null, text = "Secreto")
        cipher.destroyKey()

        assertNull(repository.observeAll().first().single().text)
    }

    @Test
    fun `deleting removes the entry`() = runTest {
        val id = repository.save(id = 0, mood = null, text = "Para borrar")
        repository.delete(id)
        assertEquals(emptyList<Any>(), repository.observeAll().first())
    }
}

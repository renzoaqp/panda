package com.panbu.app.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/** Texto cifrado y el vector de inicialización (IV) con el que se cifró. Los dos se guardan juntos. */
class EncryptedText(val cipherText: ByteArray, val iv: ByteArray)

/** Cifra y descifra textos privados (el diario). */
interface TextCipher {
    fun encrypt(plainText: String): EncryptedText

    /** Lanza una excepción si no se puede descifrar (clave borrada o datos dañados). */
    fun decrypt(encrypted: EncryptedText): String

    /** Borra la clave: lo cifrado con ella ya no se puede leer. Parte de "Borrar todos mis datos". */
    fun destroyKey()
}

/**
 * AES-256-GCM con la clave en **Android Keystore** (guardrail 13, docs/decisiones/0006-diario-cifrado.md).
 *
 * - La clave se crea dentro del Keystore y nunca sale de él: ni la app puede leerla, solo usarla.
 * - Cada texto usa un IV nuevo (lo genera el Keystore) y GCM detecta si los datos se alteraron.
 * - Con `allowBackup="false"`, el diario cifrado no viaja a otro celular; y aunque viajara, sin la clave no se
 *   podría leer.
 */
@Singleton
class KeystoreTextCipher @Inject constructor() : TextCipher {

    private val keyStore: KeyStore by lazy { KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) } }

    override fun encrypt(plainText: String): EncryptedText {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key())
        return EncryptedText(cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)), iv = cipher.iv)
    }

    override fun decrypt(encrypted: EncryptedText): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, encrypted.iv))
        return String(cipher.doFinal(encrypted.cipherText), Charsets.UTF_8)
    }

    @Synchronized
    override fun destroyKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) keyStore.deleteEntry(KEY_ALIAS)
    }

    @Synchronized
    private fun key(): SecretKey =
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey) ?: KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(KEY_BITS)
                        .build(),
                )
            }
            .generateKey()

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        /** No cambiar: con otro nombre, las entradas ya guardadas no se podrían leer. */
        const val KEY_ALIAS = "panbu_journal_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_BITS = 256
        const val TAG_BITS = 128
    }
}

package com.app.crowns.data.security

import android.util.Base64
import com.app.crowns.data.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH = 128
        const val GCM_IV_LENGTH = 12
    }

    private val secretKey: SecretKeySpec by lazy {
        val keyBytes = Base64.decode(BuildConfig.FIELD_ENCRYPTION_KEY, Base64.NO_WRAP)
        require(keyBytes.size == 32) {
            "FIELD_ENCRYPTION_KEY debe ser una clave Base64 de 256 bits (32 bytes)"
        }
        SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + ciphertext, Base64.NO_WRAP)
    }

    /**
     * Descifra un valor producido por [encrypt].
     * Si falla (valor legacy en texto plano), devuelve el valor original sin cambios.
     */
    fun decrypt(value: String): String {
        return try {
            val combined = Base64.decode(value, Base64.NO_WRAP)
            if (combined.size <= GCM_IV_LENGTH) return value
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val ciphertext = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            String(cipher.doFinal(ciphertext), Charsets.UTF_8)
        } catch (_: Exception) {
            value
        }
    }
}

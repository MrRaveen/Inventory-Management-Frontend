package secure

import javax.crypto.SecretKey

interface createKeyIniter {
    fun createSecurityKey() : SecretKey
}


import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureStorageHelper(context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("SecureStorage", Context.MODE_PRIVATE)

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val keyAlias = "com.example.SECURE_KEY"

    // Store encrypted data
    fun saveData(key: String, value: String) {
        val encrypted = encryptData(value)
        sharedPrefs.edit().putString(key, Base64.encodeToString(encrypted, Base64.NO_WRAP)).apply()
    }

    // Retrieve and decrypt data
    fun getData(key: String): String? {
        val base64Data = sharedPrefs.getString(key, null) ?: return null
        val encryptedBytes = Base64.decode(base64Data, Base64.NO_WRAP)
        return decryptData(encryptedBytes)
    }

    // Encryption (same as before)
    private fun encryptData(plaintext: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
        return iv + ciphertext
    }

    // Decryption (same as before)
    private fun decryptData(encryptedData: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = encryptedData.copyOfRange(0, GCM_IV_LENGTH)
        val ciphertext = encryptedData.copyOfRange(GCM_IV_LENGTH, encryptedData.size)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
        return String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8)
    }

    // Key management (same as before)
    private fun getOrCreateSecretKey(): SecretKey {
        if (!keyStore.containsAlias(keyAlias)) generateKey()
        return (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        keyGenerator.generateKey()
    }

    companion object {
        private const val GCM_IV_LENGTH = 12
    }
}
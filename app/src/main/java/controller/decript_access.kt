package controller
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import secure.decript_accessInter
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class decript_access : decript_accessInter {
    override fun accessToken(context: Context): String? {
        try {
            // Get the encrypted data and IV from SharedPreferences
            val sharedPrefs: SharedPreferences =
                context.getSharedPreferences("SecureStorage", Context.MODE_PRIVATE)
            val base64Data = sharedPrefs.getString("ENCRYPTED_DATA", null)
            val base64Iv = sharedPrefs.getString("IV", null)

            if (base64Data == null || base64Iv == null) {
                return null // No data or IV found
            }

            // Decode the encrypted data and IV
            val encryptedBytes = Base64.decode(base64Data, Base64.NO_WRAP)
            val iv = Base64.decode(base64Iv, Base64.NO_WRAP)

            // Get the key from KeyStore
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val key = keyStore.getKey("MySecureKey", null) as SecretKey

            // Initialize cipher for decryption
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(128, iv) // 128-bit authentication tag length
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)

            // Decrypt the data
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Log.e("TAG","Error occurred when creating AES key (createKey.kt) : " + e.toString())
            throw Exception("Error occurred when creating AES key (createKey.kt) : " + e.toString())
        }
    }
}
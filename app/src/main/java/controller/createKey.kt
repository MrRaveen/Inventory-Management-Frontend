package controller
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import secure.createKeyIniter
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
class createKey : createKeyIniter{
    override fun createSecurityKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                "MySecureKey",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        val secretKey = keyGenerator.generateKey()
        return secretKey
    }
}
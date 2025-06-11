package controller

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.gallery.MainActivity
import secure.encrypt_saveInter
import javax.crypto.Cipher
import javax.crypto.SecretKey
class encrypt_save  : encrypt_saveInter{
    override fun saveProcess(accessToken: String, secretKey : SecretKey, context : Context) {
        //encrypt process
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv // Initialization vector
        val encryptedData = cipher.doFinal(accessToken.toByteArray())
        //save process
        
    }
}
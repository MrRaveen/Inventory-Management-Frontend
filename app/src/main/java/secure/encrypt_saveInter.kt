package secure

import android.content.Context
import javax.crypto.SecretKey

interface encrypt_saveInter {
    fun saveProcess(accessToken : String, context : Context)
}
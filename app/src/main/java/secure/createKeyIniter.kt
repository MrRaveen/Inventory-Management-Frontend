package secure

import javax.crypto.SecretKey

interface createKeyIniter {
    fun createSecurityKey() : SecretKey
}

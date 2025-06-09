package controller

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gallery.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HostnameVerifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit

class handleLogin : ViewModel(){
    private val scope = CoroutineScope(Dispatchers.IO)
    private fun createClient(resources: Resources): OkHttpClient {
        resources.openRawResource(R.raw.inventorymanagement).use { inputStream ->
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificate = certificateFactory.generateCertificate(inputStream) as X509Certificate

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                setCertificateEntry("ca", certificate)
            }

            val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            ).apply {
                init(keyStore)
            }

            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, trustManagerFactory.trustManagers, null)
            }

            // Create custom hostname verifier
            val hostnameVerifier = HostnameVerifier { hostname, _ ->
                hostname == "192.168.113.42" // Verify the specific IP
            }

            return OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)    // Time to establish connection
                .readTimeout(60, TimeUnit.SECONDS)       // Time between data packets
                .writeTimeout(60, TimeUnit.SECONDS)      // Time to send request body
                .sslSocketFactory(
                    sslContext.socketFactory,
                    trustManagerFactory.trustManagers[0] as X509TrustManager
                )
                .hostnameVerifier(hostnameVerifier)
                .build()
        }
    }

    suspend fun handleProcess(
        userName: String,
        password: String,
        resources: Resources
    ):String = withContext(Dispatchers.IO) {
        try {
            val client = createClient(resources)

            // Fix JSON formatting - strings need quotes
            val jsonBody = """
                {
                    "userName": "$userName",
                    "password": "$password"
                }
            """.trimIndent()

            val body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonBody
            )

            val request = Request.Builder()
                .url("https://192.168.113.42:7113/logIn")
                .post(body)
                .build()

            val response: Response = client.newCall(request).execute()
//            val responseBody2 = response.body()?.string() ?: "Empty body"
//            Log.e("TAG","OUT : " + responseBody2)
            return@withContext response.body()?.string() ?: "Empty response"
        } catch (e: Exception) {
            Log.e("TAG","Error occurred (controller): ${e.message}")
           return@withContext "Error occurred (controller): ${e.message}"
        }
    }
}
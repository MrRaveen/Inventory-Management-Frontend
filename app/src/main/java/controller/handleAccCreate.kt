package controller

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gallery.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class handleAccCreate : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private fun createClient(resources: Resources): OkHttpClient {
        resources.openRawResource(R.raw.inventorymanagement2).use { inputStream ->
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
                hostname == "192.168.159.42" // Verify the specific IP
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
    suspend fun handleLogInProcess(
        userName: String,
        password: String,
        resources: Resources
    ):String = withContext(Dispatchers.IO) {
        try{
            var loginObj = logInEndpoints()
            val client = createClient(resources)
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
                .url(loginObj.createAccEndpoint)
                .post(body)
                .build()
            val response: Response = client.newCall(request).execute()
            return@withContext response.body()?.string() ?: "Empty response"
        }catch(e: Exception){
            Log.e("TAG","Error occured when processing create account (handleAccCreate.kt) : "+ e.toString())
            throw Exception("Error occured when processing create account (handleAccCreate.kt) : " + e.toString())
        }
    }
}
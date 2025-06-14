package controller

import Model.FolderResponseConverter
import Model.Folders
import Model.LogInOutputResponse
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gallery.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class getAllFolders : ViewModel(){
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
    suspend fun handleFolderGet(userID: Int,jwtToken : String,resources: Resources): List<Folders>? = withContext(Dispatchers.IO){
        try{
            var loginObj = logInEndpoints()
            val client = createClient(resources)
            val request = Request.Builder()
                .url(loginObj.getFoldersEndpoint + userID)
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()
            val response: Response = client.newCall(request).execute()
            val responseBody = response.body()?.string() ?: "{}"
            val authResponse = Gson().fromJson(responseBody, FolderResponseConverter::class.java)
            //FIXME:TEST
            authResponse.folderList?.forEach {item ->
                Log.e("TAG","TEST OUTPUT : " + item.name)
            }
            return@withContext null;
        }catch(e: Exception){
            throw Exception("Error occured when selecting folders (getAllFolders.kt) : " + e.toString())
        }
    }
}

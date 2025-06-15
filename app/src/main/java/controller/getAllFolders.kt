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
import com.google.gson.reflect.TypeToken

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
//    suspend fun handleFolderGet(userID: Int,jwtToken : String,resources: Resources): List<Folders>? = withContext(Dispatchers.IO){
//        try{
//            var loginObj = logInEndpoints()
//            val client = createClient(resources)
//            val request = Request.Builder()
//                .url(loginObj.getFoldersEndpoint + userID)
//                .addHeader("Authorization", "Bearer $jwtToken")
//                .build()
//            val response: Response = client.newCall(request).execute()
//            val responseBody = response.body()?.string() ?: "{}"
//            Log.e("TAG", "OUTPUT!! --> $responseBody")
////            val responseBody = response.body()?.string() ?: "{}"
////            val authResponse = Gson().fromJson(responseBody, FolderResponseConverter::class.java)
////            //FIXME:TEST
////            authResponse.folderList?.forEach {item ->
////                Log.e("TAG","TEST OUTPUT : " + item.name)
////            }
//            return@withContext null;
//        }catch(e: Exception){
//            throw Exception("Error occured when selecting folders (getAllFolders.kt) : " + e.toString())
//        }
//    }
suspend fun handleFolderGet(userID: Int, jwtToken: String, resources: Resources): List<Folders>? =
    withContext(Dispatchers.IO) {
        try {
            val loginObj = logInEndpoints()
            val client = createClient(resources)

            // FIX: Use correct endpoint property
            val url = loginObj.getFoldersEndpoint + userID
            Log.d("Network", "Fetching folders from: $url")

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()

            val response: Response = client.newCall(request).execute()
            val responseBody = response.body()?.string() ?: "[]"
            Log.e("TAG", "Response body: $responseBody")

            // Check HTTP status
            if (!response.isSuccessful) {
                Log.e("Network", "HTTP error: ${response.code()} - ${response.message()}")
                return@withContext null
            }

            // FIX: Correct JSON parsing for array response
            val typeToken = object : TypeToken<List<Folders>>() {}.type
            val folderList = Gson().fromJson<List<Folders>>(responseBody, typeToken) ?: emptyList()

            // Log results
            Log.e("Network", "Fetched ${folderList.size} folders")
            folderList.forEach { folder ->
                Log.e("FolderData", "ID: ${folder.folderID}, Name: ${folder.name}")
            }

            return@withContext folderList
        } catch (e: Exception) {
            Log.e("Network", "Error fetching folders", e)
            throw Exception("Error occurred when selecting folders: ${e.message}")
        }
    }
}
package controller

import java.net.HttpURLConnection

class handleLogin {
    fun handleProcess(userName: String,password: String) : Int{
        try {
            val endpointsObj = logInEndpoints()
            var url = endpointsObj.logInEndpoint
            //sending the request
            
            return 1
        }catch (e: Exception){
            throw Exception("Error occured when handling login : " + e.toString())
        }
    }
}
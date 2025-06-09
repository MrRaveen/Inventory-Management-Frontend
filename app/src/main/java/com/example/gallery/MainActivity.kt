package com.example.gallery

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import controller.handleLogin
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val logInButton = findViewById<Button>(R.id.btnLogin)
        val userName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editUserName)
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editPassword)
        logInButton.setOnClickListener {
           try{
               if(userName.text.toString() == "" || password.text.toString() == ""){
                   Toast.makeText(
                       this, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                       "Username or password is null",
                       Toast.LENGTH_SHORT // Add duration
                   ).show()
               }else{
                   //send the request
                   val result: handleLogin by viewModels()
                   var result2 : String = ""
                   var messageOut : String = ""
                   lifecycleScope.launch {
                       result2 = result.handleProcess(userName.text.toString(), password.text.toString(), resources)
//                       if(result2.isNotEmpty()){
//
//                       }else{
//                           messageOut = "Token is not generated"
//                       }
                       messageOut = result2
                       //FIXME: TEST
                       Toast.makeText(
                           this@MainActivity,
                           messageOut,
                           Toast.LENGTH_LONG // Add duration
                       ).show()
                   }
               }
           }catch (ex: Exception){
               Log.e("TAG","Error occured when login (view): " + ex.toString())
               Toast.makeText(
                   this, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                   "Error occured when login (view): " + ex.toString(),
                   Toast.LENGTH_SHORT // Add duration
               ).show()
           }
        }
    }
}
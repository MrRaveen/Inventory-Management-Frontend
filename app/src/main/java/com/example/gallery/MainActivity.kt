package com.example.gallery

import Model.LogInOutputResponse
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import controller.createKey
import controller.decript_access
import controller.encrypt_save
import controller.handleLogin
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val logInButton = findViewById<Button>(R.id.btnLogin)
        val userName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editUserName)
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editPassword)
        val createAccValue = findViewById<TextView>(R.id.txtSignUp)
        createAccValue.setOnClickListener{
            val intent = Intent(this, createAcc::class.java)
            startActivity(intent)
        }
        logInButton.setOnClickListener {
            val PREF_NAME = "prefs"
            val KEY_NAME = "userid"
            try{
               if(userName.text.toString() == "" || password.text.toString() == ""){
                   Toast.makeText(
                       this, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                       "Username or password is null",
                       Toast.LENGTH_SHORT // Add duration
                   ).show()
               }else{
                   val loadingDialog = Dialog(this)
                   loadingDialog.setContentView(R.layout.custom_dialog)
                   loadingDialog.window?.setLayout(
                       LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT
                   )
                   loadingDialog.setCancelable(false)
                   loadingDialog.show()
                   //send the request
                   val result: handleLogin by viewModels() //in here
                   var result2 : LogInOutputResponse?
                   var messageOut : String = ""
                   lifecycleScope.launch {
                       result2 = result.handleProcess(userName.text.toString(), password.text.toString(), resources)
//                       messageOut = result2
                       loadingDialog.dismiss()
//                       Toast.makeText(
//                           this@MainActivity, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
//                           result2?.userID.toString() + "\n" + result2?.token,
//                           Toast.LENGTH_SHORT // Add duration
//                       ).show()
                       if(result2 == null){
                           Toast.makeText(
                               this@MainActivity,
                               "Invalid username or password",
                               Toast.LENGTH_LONG // Add duration
                           ).show()
                       }else{
                           if(result2!!.token == null || result2!!.userID == 0){
                               Toast.makeText(
                                   this@MainActivity,
                                   "Something went wrong. Try again",
                                   Toast.LENGTH_LONG // Add duration
                               ).show()
                           }else{
                               //save the token in android keystore
                               var securityKeyObj = createKey()
                               var createdSecKey = securityKeyObj.createSecurityKey()
                               var saveObj = encrypt_save()
                               saveObj.saveProcess(result2!!.token,this@MainActivity)
                               //TODO: SAVE THE USER ID
                               var sharedPref : SharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                               val editor = sharedPref.edit().putString(KEY_NAME, result2!!.userID.toString()).apply()
                               //go to another activity (main menu)
                               val intent = Intent(this@MainActivity, homeMenu::class.java)
                               startActivity(intent)
                           }
                       }
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
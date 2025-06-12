package com.example.gallery

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import controller.handleAccCreate
import controller.handleLogin
import kotlinx.coroutines.launch

class createAcc : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_acc)
        val signUpButtonValue = findViewById<Button>(R.id.signupButton)
        val userNameInputValue = findViewById<TextInputEditText>(R.id.enterNewUserName)
        val passwordInputValue = findViewById<TextInputEditText>(R.id.enterNewPassword)
        signUpButtonValue.setOnClickListener {
            try{
                if(userNameInputValue.text.toString().isNotEmpty() && passwordInputValue.text.toString().isNotEmpty()){
                    //loading screen
                    val loadingDialog = Dialog(this)
                    loadingDialog.setContentView(R.layout.custom_dialog)
                    loadingDialog.window?.setLayout(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    loadingDialog.setCancelable(false)
                    loadingDialog.show()
                    //send request
                    val accCreateObj: handleAccCreate by viewModels() //in here
                    var messageOut : String = ""
                    lifecycleScope.launch {
                        messageOut = accCreateObj.handleLogInProcess(userNameInputValue.text.toString(),passwordInputValue.text.toString(), resources)
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@createAcc, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                            "Result : " + messageOut,
                            Toast.LENGTH_SHORT // Add duration
                        ).show()
                        val intent = Intent(this@createAcc, MainActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    Toast.makeText(
                        this, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                        "Username or password is empty",
                        Toast.LENGTH_SHORT // Add duration
                    ).show()
                }
            }catch(e: Exception){
                Toast.makeText(
                    this, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                    "Error occured whan creating userAccout (createAcc.kt) : " + e.toString(),
                    Toast.LENGTH_SHORT // Add duration
                ).show()
            }
        }
    }
}
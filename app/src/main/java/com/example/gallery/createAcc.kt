package com.example.gallery

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

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
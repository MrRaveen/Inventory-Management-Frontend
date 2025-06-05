package com.example.gallery

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val logInButton = findViewById<Button>(R.id.btnLogin)
        val userName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editUserName)
        val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editPassword)
        logInButton.setOnClickListener {
            if(userName.text.toString() == "" || password.text.toString() == ""){
                Toast.makeText(
                    this, // Use the Activity's context (replace "MainActivity" with your actual Activity name)
                    "Username or password is null",
                    Toast.LENGTH_SHORT // Add duration
                ).show()
            }else{
                //send the request
            }

        }
    }
}
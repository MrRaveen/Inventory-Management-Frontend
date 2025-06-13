package com.example.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.gallery.databinding.ActivityHomeMenuBinding
import com.example.gallery.databinding.ActivityMainBinding

class homeMenu : AppCompatActivity() {
    private lateinit var binding : ActivityHomeMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(homePart())
        binding.bottomViewPart.setOnItemSelectedListener{
            when(it.itemId){
                R.id.homeBottom -> replaceFragment(homePart())
                R.id.addbottom -> replaceFragment(addInventoryPart())
                R.id.settingsbottom -> replaceFragment(settingsPart())
                else -> {

                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}
package com.matamapp.matam

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val icon = findViewById<ImageView>(R.id.icon)
        icon.setOnClickListener{ goToHome() }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
    }
}
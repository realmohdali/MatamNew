package com.matamapp.matam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.matamapp.matam.CommonData.Companion.isConnectToInternet

class OfflineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        val isConnect: Button = findViewById(R.id.connectionCheckButton)

        isConnect.setOnClickListener {
            if (isConnectToInternet(this)) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
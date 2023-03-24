package com.matamapp.matam

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.matamapp.matam.CommonData.Companion.NOTIFICATION_CHANNEL_ID
import com.matamapp.matam.CommonData.Companion.isConnectToInternet

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        manageNotifications()
    }

    private fun manageNotifications() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Matam App Notification Channel"
            val description = "Notification channel to show media control notification"
            val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationManager.IMPORTANCE_LOW
            } else {
                0
            }
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!notificationManager.areNotificationsEnabled()) {
                startActivity(Intent(this, PermissionActivity::class.java))
            } else {
                if (isConnectToInternet(this)) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, OfflineActivity::class.java))
                }
            }
        }
    }
}
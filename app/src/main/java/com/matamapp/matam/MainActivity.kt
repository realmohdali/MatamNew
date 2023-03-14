package com.matamapp.matam

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.matamapp.matam.CommonData.Companion.NOTIFICATION_CHANNEL_ID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!notificationManager.areNotificationsEnabled()) {
                // Notification permission is not granted, request it
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Matam App Notification Channel"
                    val description = "Notification channel to show media control notification"
                    val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        NotificationManager.IMPORTANCE_HIGH
                    } else {
                        0
                    }
                    val channel =
                        NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                            this.description = description
                        }
                    notificationManager.createNotificationChannel(channel)
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Enable Notification")
                    .setMessage("Notifications are required for proper functioning of the application")
                    .setPositiveButton("Settings") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                builder.show()
            } else {
                Handler().postDelayed({
                    goHome()
                }, 3000)
            }
    }
    //        val sharedPreferences = getSharedPreferences(CommonData.PREFERENCES, MODE_PRIVATE)
//        val userToken = sharedPreferences.getString(CommonData.SESSION_TOKEN, "0")
//
//        if (userToken == "0") {
//            goToLogin()
//        } else {
//            val url = CommonData.API_URL + "check-session-validity.php"
//            val stringRequest = object : StringRequest(
//                Method.POST,
//                url,
//                Response.Listener { response ->
//                    val jsonObject = JSONObject(response)
//                    val code = jsonObject.getString("code")
//                    if (code == "200") {
//                        startActivity(Intent(this, HomeActivity::class.java))
//                        finish()
//                    } else {
//                        goToLogin()
//                    }
//                },
//                Response.ErrorListener { }) {
//                override fun getParams(): MutableMap<String, String> {
//                    val params = HashMap<String, String>()
//                    params["platform_token"] = CommonData.PLATFORM_TOKEN
//                    params["session_token"] = userToken.toString()
//
//                    return params
//                }
//            }
//            Volley.newRequestQueue(this).add(stringRequest)
//        }

    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

//    private fun goToLogin() {
//        startActivity(Intent(this, LoginActivity::class.java))
//        finish()
//    }
}
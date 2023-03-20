package com.matamapp.matam

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class PermissionActivity : AppCompatActivity() {

    private lateinit var requestLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                //Permission Granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                //Permission not Granted
                showErrorMessage()
            }
        }

        findViewById<Button>(R.id.allow_button).setOnClickListener {
            askForNotificationPermission()
        }
    }

    @SuppressLint("InlinedApi")
    private fun askForNotificationPermission() {
        requestLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun showErrorMessage() {
        Toast.makeText(
            this,
            "Permission not Granted. Go to app settings and enable notifications.",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }
}
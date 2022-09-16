package com.matamapp.matam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val oldPassword = findViewById<EditText>(R.id.old_password)
        val newPassword = findViewById<EditText>(R.id.new_password)
        val newPasswordConfirm = findViewById<EditText>(R.id.new_password_confirm)
        val changePassword = findViewById<Button>(R.id.submit_button)

        changePassword.setOnClickListener {
            val oldPasswordText = oldPassword.text.toString()
            val newPasswordText = newPassword.text.toString()
            val newPasswordConfirmText = newPasswordConfirm.text.toString()

            if (oldPasswordText.isEmpty()) {
                Toast.makeText(this, "Old Password should not be empty", Toast.LENGTH_SHORT).show()
            } else if (newPasswordText.isEmpty()) {
                Toast.makeText(this, "New Password should not be empty", Toast.LENGTH_SHORT).show()
            } else if (newPasswordConfirmText.isEmpty()) {
                Toast.makeText(this, "Confirm New Password should not be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (newPasswordText != newPasswordConfirmText) {
                Toast.makeText(this, "New Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "proceeding", Toast.LENGTH_SHORT).show()
                val url = CommonData.API_URL + "change-password.php"
            }
        }
    }
}
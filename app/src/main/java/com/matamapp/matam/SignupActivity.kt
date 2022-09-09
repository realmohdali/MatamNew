package com.matamapp.matam

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val fullName = findViewById<EditText>(R.id.fullNameSignup)
        val email = findViewById<EditText>(R.id.emailSignup)
        val password = findViewById<TextInputEditText>(R.id.passwordSignup)
        val confirmPassword = findViewById<EditText>(R.id.confirmPasswordSignup)
        val signup = findViewById<Button>(R.id.signupButtonSignup)
        val alreadyRegistered = findViewById<TextView>(R.id.alreadyRegisteredSignup)

        fullName.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


        signup.setOnClickListener {
            val fullNameText = fullName.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            if (fullNameText.isEmpty()) {
                Toast.makeText(applicationContext, "Full name is required", Toast.LENGTH_SHORT)
                    .show()
            } else if (emailText.isEmpty()) {
                Toast.makeText(applicationContext, "Email is required", Toast.LENGTH_SHORT)
                    .show()
            } else if (passwordText.isEmpty()) {
                Toast.makeText(applicationContext, "Password is required", Toast.LENGTH_SHORT)
                    .show()
            } else if (confirmPasswordText.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Confirm Password is required",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!passwordText.equals(confirmPasswordText)) {
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(applicationContext, "Signing up", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        alreadyRegistered.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
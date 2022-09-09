package com.matamapp.matam

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var forgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)
        forgotPassword = findViewById(R.id.forgetPasswordLogin)

        email.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        loginButton.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            if (emailText.isEmpty()) {
                Toast.makeText(applicationContext, "Email should not be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (passwordText.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Password should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Checking the login details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        forgotPassword.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Forgot Password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
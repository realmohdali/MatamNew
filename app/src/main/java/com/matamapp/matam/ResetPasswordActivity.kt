package com.matamapp.matam

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var resend: TextView
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val activityIntent = intent
        email = activityIntent.extras?.getString("email").toString()

        val resetCode = findViewById<EditText>(R.id.code)
        val newPassword = findViewById<EditText>(R.id.new_password)
        val newPasswordConfirm = findViewById<EditText>(R.id.new_password_confirm)
        val resetButton = findViewById<Button>(R.id.submit_button)
        val warningText = findViewById<TextView>(R.id.warning_text)
        resend = findViewById(R.id.resend)

        Timer("waitingForResend", false).schedule(30000L) {
            runOnUiThread { showResendOption() }
        }

        resetButton.setOnClickListener {
            val resetCodeText = resetCode.text.toString()
            val newPasswordText = newPassword.text.toString()
            val newPasswordConfirmText = newPasswordConfirm.text.toString()

            if (resetCodeText.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Password Reset Code should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (newPasswordText.isEmpty()) {
                Toast.makeText(
                    applicationContext, "New Password should not be empty", Toast.LENGTH_SHORT
                ).show()
            } else if (newPasswordConfirmText.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Confirm New Password should not be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (newPasswordText != newPasswordConfirmText) {
                Toast.makeText(
                    applicationContext, "New Passwords does not match", Toast.LENGTH_SHORT
                ).show()
            } else {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.loading_dialog)

                dialog.show()

                val url = CommonData.API_URL + "reset-password.php"

                val stringRequest =
                    object : StringRequest(Method.POST, url, Response.Listener { response ->
                        val jsonObject = JSONObject(response)
                        when (jsonObject.getString("code")) {
                            "15" -> {
                                val warning =
                                    "Password Reset Code is invalid\nPlease check your email for the code"
                                warningText.text = warning
                                dialog.dismiss()
                            }
                            "200" -> {
                                Toast.makeText(
                                    this, "Password Changed Successfully", Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                errorToast()
                                dialog.dismiss()
                            }
                        }
                    }, Response.ErrorListener {
                        errorToast()
                        dialog.dismiss()
                    }) {
                        override fun getParams(): MutableMap<String, String> {
                            val params = HashMap<String, String>()
                            params["token"] = resetCodeText
                            params["new-password"] = newPasswordText

                            return params
                        }
                    }

                Volley.newRequestQueue(this).add(stringRequest)
            }
        }
    }

    private fun showResendOption() {
        resend.visibility = View.VISIBLE
        resend.setOnClickListener {
            resendCode()
        }
    }

    private fun resendCode() {
        resend.visibility = View.GONE
        Timer("waitingForResend", false).schedule(30000L) {
            runOnUiThread { showResendOption() }
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog)

        dialog.show()

        val url = CommonData.API_URL + "request-password-reset.php"
        val stringRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->
            val jsonObject = JSONObject(response)
            when (jsonObject.getString("code")) {
                "200" -> {
                    Toast.makeText(
                        this, "Code resent. Please check your email.", Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
                else -> {
                    dialog.dismiss()
                    errorToast()
                }
            }
        }, Response.ErrorListener {
            dialog.dismiss()
            errorToast()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun errorToast() {
        Toast.makeText(
            applicationContext, "Something went wrong", Toast.LENGTH_SHORT
        ).show()
    }
}
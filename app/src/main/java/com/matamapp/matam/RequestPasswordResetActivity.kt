package com.matamapp.matam

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class RequestPasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_password_reset)
        val emailId = findViewById<EditText>(R.id.mail_id)
        val requestCode = findViewById<Button>(R.id.submit_button)
        val invalidEmail = findViewById<TextView>(R.id.invalid_email)

        invalidEmail.visibility = View.GONE

        emailId.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        requestCode.setOnClickListener {
            invalidEmail.visibility = View.GONE

            val emailIdText = emailId.text.toString()
            if (emailIdText.isEmpty()) {
                Toast.makeText(this, "Email id is required", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailIdText).matches()) {
                Toast.makeText(this, "Please enter a valid email id", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.loading_dialog)

                dialog.show()

                val url = CommonData.API_URL + "request-password-reset.php"
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener { response ->
                        val jsonObject = JSONObject(response)
                        when (jsonObject.getString("code")) {
                            "13" -> {
                                invalidEmail.visibility = View.VISIBLE
                                dialog.dismiss()
                            }
                            "200" -> {
                                intent = Intent(this, ResetPasswordActivity::class.java)
                                intent.putExtra("email", emailIdText)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                dialog.dismiss()
                                errorToast()
                            }
                        }
                    },
                    Response.ErrorListener {
                        dialog.dismiss()
                        errorToast()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["platform_token"] = CommonData.PLATFORM_TOKEN
                        params["email"] = emailIdText
                        return params
                    }
                }
                Volley.newRequestQueue(this).add(stringRequest)
            }
        }
    }

    private fun errorToast() {
        Toast.makeText(
            applicationContext,
            "Something went wrong",
            Toast.LENGTH_SHORT
        ).show()
    }
}
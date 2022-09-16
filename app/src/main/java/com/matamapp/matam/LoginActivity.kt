package com.matamapp.matam

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val loginEmail = findViewById<EditText>(R.id.loginEmail)
        val loginPassword = findViewById<EditText>(R.id.loginPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val forgetPasswordLogin = findViewById<TextView>(R.id.forgetPasswordLogin)
        loginEmail.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


        loginButton.setOnClickListener {
            val emailText = loginEmail.text.toString()
            val passwordText = loginPassword.text.toString()
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
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.loading_dialog)

                dialog.show()

                val url = CommonData.API_URL + "login.php"
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener { response ->
                        val jsonObject = JSONObject(response)
                        val code = jsonObject.getString("code")
                        if (code == "200") {
                            val sharedPreferences =
                                getSharedPreferences(CommonData.PREFERENCES, MODE_PRIVATE)
                            val preferenceEditor = sharedPreferences.edit()
                            preferenceEditor.putString(
                                CommonData.SESSION_TOKEN,
                                jsonObject.getString("data")
                            )
                            preferenceEditor.apply()

                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            errorToast()
                            dialog.dismiss()
                        }
                    },
                    Response.ErrorListener {
                        errorToast()
                        dialog.dismiss()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["platform_token"] = CommonData.PLATFORM_TOKEN
                        params["email"] = emailText
                        params["password"] = passwordText
                        params["device"] = CommonData.DEVICE
                        return params
                    }
                }
                Volley.newRequestQueue(this).add(stringRequest)

            }
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        forgetPasswordLogin.setOnClickListener {
            startActivity(Intent(this, RequestPasswordResetActivity::class.java))
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
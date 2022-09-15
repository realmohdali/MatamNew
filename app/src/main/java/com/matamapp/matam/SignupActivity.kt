package com.matamapp.matam

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        fullNameSignup.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


        signupButtonSignup.setOnClickListener {
            val fullNameText = fullNameSignup.text.toString()
            val emailText = emailSignup.text.toString()
            val passwordText = passwordSignup.text.toString()
            val confirmPasswordText = confirmPasswordSignup.text.toString()

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
            } else if (passwordText != confirmPasswordText) {
                Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.loading_dialog)

                dialog.show()

                val url = CommonData.API_URL + "add-new-user.php"
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
                        params["name"] = fullNameText
                        params["email"] = emailText
                        params["password"] = passwordText
                        params["device"] = CommonData.DEVICE
                        return params
                    }
                }

                Volley.newRequestQueue(this).add(stringRequest)
            }
        }
        alreadyRegisteredSignup.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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
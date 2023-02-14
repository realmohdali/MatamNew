package com.matamapp.matam

import android.app.Dialog
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

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val oldPassword = findViewById<EditText>(R.id.old_password)
        val newPassword = findViewById<EditText>(R.id.new_password)
        val newPasswordConfirm = findViewById<EditText>(R.id.new_password_confirm)
        val changePassword = findViewById<Button>(R.id.submit_button)
        val incorrectPassword = findViewById<TextView>(R.id.incorrect_password)

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
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.loading_dialog)

                dialog.show()
                incorrectPassword.visibility = View.GONE
                val url = CommonData.API_URL + "change-password.php"
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener { response ->
                        val jsonObject = JSONObject(response)
                        when (jsonObject.getString("code")) {
                            "200" -> {
                                Toast.makeText(
                                    applicationContext,
                                    "Password changed successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                            "18" -> {
                                incorrectPassword.visibility = View.VISIBLE
                                dialog.dismiss()
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
                        val preferences = getSharedPreferences(CommonData.PREFERENCES, MODE_PRIVATE)
                        val sessionToken =
                            preferences.getString(CommonData.SESSION_TOKEN, "0").toString()
                        val params = HashMap<String, String>()
                        params["session_token"] = sessionToken
                        params["old-password"] = oldPasswordText
                        params["new-password"] = newPasswordText
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
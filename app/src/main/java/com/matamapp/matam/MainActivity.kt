package com.matamapp.matam

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
                              goHome()
        }, 3000)

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

    private fun goHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
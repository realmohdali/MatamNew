package com.matamapp.matam

import android.os.Build

class CommonData {
    companion object {
        const val PLATFORM_TOKEN = "r7u9VmbOyovnvOZw"
        const val API_URL = "https://matam.syedmohdali.com/api/"
        const val PREFERENCES = "preferences"
        const val SESSION_TOKEN = "session_token"
        val DEVICE = Build.MANUFACTURER + " " + Build.MODEL
    }
}
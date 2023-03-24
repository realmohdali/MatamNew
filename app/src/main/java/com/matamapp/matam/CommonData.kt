package com.matamapp.matam

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.matamapp.matam.db.DatabaseHelper
import com.matamapp.matam.db.FavoriteData

class CommonData {
    companion object {
        const val API_URL = "https://matamapp.syedmohdali.com/api/"
        const val PREFERENCES = "preferences"
        const val SESSION_TOKEN = "session_token"
        val DEVICE = Build.MANUFACTURER + " " + Build.MODEL
        var serviceRunning = false
        const val FAVORITE_TABLE = "favorites"
        const val NOTIFICATION_CHANNEL_ID = "matam_app_notification_channel"

        fun addFavorite(context: Context, track: FavoriteData) {
            val databaseHelper = DatabaseHelper.getInstance(context)
            databaseHelper.favoriteDao().addToFavorite(track)
        }

        fun removeFavorite(context: Context, id: String) {
            val databaseHelper = DatabaseHelper.getInstance(context)
            databaseHelper.favoriteDao().removeFromFavorite(id)
        }

        fun existsInFavorite(context: Context, id: String): Boolean {
            val databaseHelper = DatabaseHelper.getInstance(context)
            val trackArray: ArrayList<FavoriteData> =
                databaseHelper.favoriteDao().isFavorite(id) as ArrayList<FavoriteData>
            if (trackArray.isEmpty()) {
                return false
            }
            return true
        }

        fun isConnectToInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }
    }
}
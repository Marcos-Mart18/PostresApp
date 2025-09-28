package com.marcos.postresapp.data.local
import android.content.Context
class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().apply {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)
    fun getRefreshToken(): String? = prefs.getString("REFRESH_TOKEN", null)
}
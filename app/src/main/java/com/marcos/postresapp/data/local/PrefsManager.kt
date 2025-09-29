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

    fun saveUserInfo(username: String, roles: List<String>, profileFotoUrl: String?) {
        prefs.edit().apply {
            putString("USERNAME", username)
            putString("ROLES", roles.joinToString(","))
            putString("PROFILE_FOTO_URL", profileFotoUrl)
            apply()
        }
    }
    fun getUsername(): String? = prefs.getString("USERNAME", null)
    fun getRoles(): List<String> = prefs.getString("ROLES", "")?.split(",") ?: emptyList()
    fun getProfileFotoUrl(): String? = prefs.getString("PROFILE_FOTO_URL", null)

    fun getAccessToken(): String? = prefs.getString("ACCESS_TOKEN", null)
    fun getRefreshToken(): String? = prefs.getString("REFRESH_TOKEN", null)
}
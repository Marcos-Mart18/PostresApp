package com.marcos.postresapp.presentation.ui.utils

import android.content.Context
import android.util.Log
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.dto.auth.RefreshTokenRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenRefreshHelper {
    
    suspend fun refreshTokenIfNeeded(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val prefs = com.marcos.postresapp.di.ServiceLocator.getPrefsManager(context)
                val refreshToken = prefs.getRefreshToken()
                
                if (refreshToken == null) {
                    Log.e("TokenRefresh", "No refresh token available")
                    return@withContext null
                }
                
                // Crear cliente para refresh
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.40.26.157:9090/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                val authService = retrofit.create(AuthApiService::class.java)
                val request = RefreshTokenRequestDto(refreshToken)
                val response = authService.refreshAccessToken(request)
                
                // Guardar nuevo token
                prefs.saveAccessToken(response.accessToken)
                Log.d("TokenRefresh", "Token renovado exitosamente")
                
                return@withContext response.accessToken
                
            } catch (e: Exception) {
                Log.e("TokenRefresh", "Error al renovar token: ${e.message}")
                if (e.message?.contains("401") == true) {
                    Log.e("TokenRefresh", "Refresh token también expirado")
                }
                return@withContext null
            }
        }
    }
}
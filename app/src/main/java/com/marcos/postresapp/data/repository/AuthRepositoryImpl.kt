package com.marcos.postresapp.data.repository

import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.models.LoginRequest
import com.marcos.postresapp.data.remote.models.LoginResponse
import com.marcos.postresapp.data.remote.models.RefreshTokenRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val prefs: PrefsManager
) {
    suspend fun login(username: String, password: String): LoginResponse {
        val response = api.login(LoginRequest(username, password))
        prefs.saveTokens(response.accessToken, response.refreshToken)
        response.user.apply {
            prefs.saveUserInfo(username, roles, profileFotoUrl)
        }
        return response
    }

    suspend fun refreshAccessToken(refreshToken: String): String {
        try {
            // Crear el objeto del request
            val refreshRequest = RefreshTokenRequest(refreshToken)

            // Llamar directamente al API
            val response = api.refreshAccessToken(refreshRequest)

            // Retornar el nuevo access token
            return response.accessToken
        } catch (e: Exception) {
            throw Exception("Error refreshing access token", e)
        }
    }



    suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = prefs.getRefreshToken() ?: throw Exception("Refresh token no disponible")

            val requestBody = mapOf("refreshToken" to refreshToken)

            api.logout(request = requestBody)

            // 3. Limpiar los tokens y otros datos de la aplicación
            prefs.clearUserData()

            // 4. Retornar un resultado exitoso
            Result.success(Unit)
        } catch (e: Exception) {
            // Si ocurre un error, puedes manejarlo aquí y devolver un resultado con error
            Result.failure(e)
        }
    }

}
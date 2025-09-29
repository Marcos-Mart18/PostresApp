package com.marcos.postresapp.data.repository

import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.models.LoginRequest
import com.marcos.postresapp.data.remote.models.LoginResponse
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

    private suspend fun refreshAccessToken(refreshToken: String): String {
        // Crear el body de la solicitud con el refresh token
        val refreshTokenRequestBody = RequestBody.create(
            "application/x-www-form-urlencoded".toMediaTypeOrNull(),
            "refresh_token=$refreshToken"
        )

        try {
            // Realizar la solicitud al servidor para obtener un nuevo access token
            val response = api.refreshAccessToken(refreshTokenRequestBody)

            // Suponiendo que la respuesta contiene el nuevo token en formato JSON
            val newAccessToken = response.accessToken // Asumiendo que la respuesta tiene un campo 'accessToken'

            // Guardar el nuevo token en PrefsManager
            prefs.saveAccessToken(newAccessToken)

            return newAccessToken
        } catch (e: Exception) {
            // Manejar errores si no se puede refrescar el token (por ejemplo, si el refresh token también ha expirado)
            throw e
        }
    }

}
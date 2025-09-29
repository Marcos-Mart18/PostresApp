package com.marcos.postresapp.data.repository

import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.models.LoginRequest
import com.marcos.postresapp.data.remote.models.LoginResponse
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
}
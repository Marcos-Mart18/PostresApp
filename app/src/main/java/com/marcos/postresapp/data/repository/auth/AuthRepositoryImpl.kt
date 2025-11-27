package com.marcos.postresapp.data.repository.auth

import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.mapper.auth.toDomain
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.dto.auth.LoginRequestDto
import com.marcos.postresapp.data.remote.dto.auth.RefreshTokenRequestDto
import com.marcos.postresapp.domain.model.User
import com.marcos.postresapp.domain.repository.auth.AuthRepository

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val prefsManager: PrefsManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Pair<User, Pair<String, String>>> {
        return try {
            val request = LoginRequestDto(username, password)
            val response = authApiService.login(request)
            
            // Guardar tokens
            prefsManager.saveTokens(response.accessToken, response.refreshToken)
            
            // Guardar info del usuario
            val user = response.toDomain()
            prefsManager.saveUserInfo(
                username = user.username,
                roles = user.roles,
                profileFotoUrl = user.profileFotoUrl
            )
            
            // Retornar usuario y tokens
            val tokens = Pair(response.accessToken, response.refreshToken)
            Result.success(Pair(user, tokens))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): Result<String> {
        return try {
            val request = RefreshTokenRequestDto(refreshToken)
            val response = authApiService.refreshAccessToken(request)
            
            // Guardar el nuevo access token
            prefsManager.saveAccessToken(response.accessToken)
            
            Result.success(response.accessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = prefsManager.getRefreshToken()
            if (refreshToken != null) {
                val request = mapOf("refreshToken" to refreshToken)
                authApiService.logout(request)
            }
            
            // Limpiar datos locales
            prefsManager.clearUserData()
            
            Result.success(Unit)
        } catch (e: Exception) {
            // Aunque falle la API, limpiamos los datos locales
            prefsManager.clearUserData()
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return prefsManager.getAccessToken() != null
    }

    override fun getUserRoles(): List<String> {
        return prefsManager.getRoles()
    }
}

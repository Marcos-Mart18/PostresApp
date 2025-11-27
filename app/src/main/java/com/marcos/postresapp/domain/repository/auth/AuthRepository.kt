package com.marcos.postresapp.domain.repository.auth

import com.marcos.postresapp.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Pair<User, Pair<String, String>>>
    suspend fun refreshAccessToken(refreshToken: String): Result<String>
    suspend fun logout(): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun getUserRoles(): List<String>
}

package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.data.remote.models.LoginRequest
import com.marcos.postresapp.data.remote.models.LoginResponse
import com.marcos.postresapp.data.remote.models.RefreshTokenRequest
import com.marcos.postresapp.data.remote.models.RefreshTokenResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/v1/auth/refresh-token")
    suspend fun refreshAccessToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): RefreshTokenResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: Map<String, String>)
}
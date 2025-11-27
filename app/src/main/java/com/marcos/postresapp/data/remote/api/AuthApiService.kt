package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.data.remote.dto.auth.LoginRequestDto
import com.marcos.postresapp.data.remote.dto.auth.LoginResponseDto
import com.marcos.postresapp.data.remote.dto.auth.RefreshTokenRequestDto
import com.marcos.postresapp.data.remote.dto.auth.RefreshTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("api/v1/auth/refresh")
    suspend fun refreshAccessToken(
        @Body refreshTokenRequest: RefreshTokenRequestDto
    ): RefreshTokenResponseDto

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: Map<String, String>)
}
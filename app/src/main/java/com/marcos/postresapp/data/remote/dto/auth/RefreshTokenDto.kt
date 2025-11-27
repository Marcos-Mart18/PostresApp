package com.marcos.postresapp.data.remote.dto.auth

data class RefreshTokenRequestDto(
    val refreshToken: String
)

data class RefreshTokenResponseDto(
    val accessToken: String
)

package com.marcos.postresapp.data.remote.dto.auth

data class LoginResponseDto(
    val idUsuario: Long,
    val username: String,
    val roles: List<String>,
    val profileFotoUrl: String?,
    val accessToken: String,
    val refreshToken: String
)

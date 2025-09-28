package com.marcos.postresapp.data.remote.models

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)

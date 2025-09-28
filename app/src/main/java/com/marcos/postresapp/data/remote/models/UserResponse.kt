package com.marcos.postresapp.data.remote.models

data class UserResponse(
    val idUsuario: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val profileFotoUrl: String?,
    val username: String,
    val roles: List<String>
)
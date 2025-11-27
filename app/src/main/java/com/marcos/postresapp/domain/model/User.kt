package com.marcos.postresapp.domain.model

data class User(
    val idUsuario: Long,
    val username: String,
    val profileFotoUrl: String?,
    val roles: List<String>
)

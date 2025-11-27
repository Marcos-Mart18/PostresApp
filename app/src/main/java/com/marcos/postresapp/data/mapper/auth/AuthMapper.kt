package com.marcos.postresapp.data.mapper.auth

import com.marcos.postresapp.data.remote.dto.auth.LoginResponseDto
import com.marcos.postresapp.domain.model.User

fun LoginResponseDto.toDomain(): User {
    return User(
        idUsuario = idUsuario,
        username = username,
        profileFotoUrl = profileFotoUrl,
        roles = roles
    )
}

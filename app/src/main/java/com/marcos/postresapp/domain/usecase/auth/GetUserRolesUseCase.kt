package com.marcos.postresapp.domain.usecase.auth

import com.marcos.postresapp.domain.repository.auth.AuthRepository

class GetUserRolesUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): List<String> {
        return authRepository.getUserRoles()
    }
}

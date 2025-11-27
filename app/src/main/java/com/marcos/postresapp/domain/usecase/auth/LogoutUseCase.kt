package com.marcos.postresapp.domain.usecase.auth

import com.marcos.postresapp.domain.repository.auth.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

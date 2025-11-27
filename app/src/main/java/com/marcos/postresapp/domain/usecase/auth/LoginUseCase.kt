package com.marcos.postresapp.domain.usecase.auth

import com.marcos.postresapp.domain.model.User
import com.marcos.postresapp.domain.repository.auth.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        // Validaciones de negocio
        if (username.isBlank()) {
            return Result.failure(Exception("El usuario no puede estar vacío"))
        }
        
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña no puede estar vacía"))
        }
        
        if (password.length < 4) {
            return Result.failure(Exception("La contraseña debe tener al menos 4 caracteres"))
        }
        
        // Llamar al repositorio
        return authRepository.login(username, password).map { it.first }
    }
}

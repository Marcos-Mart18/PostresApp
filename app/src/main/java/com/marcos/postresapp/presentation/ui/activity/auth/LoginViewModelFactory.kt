package com.marcos.postresapp.presentation.ui.activity.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcos.postresapp.domain.usecase.auth.LoginUseCase
import com.marcos.postresapp.presentation.viewmodel.auth.LoginViewModel

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val authRepository: com.marcos.postresapp.domain.repository.auth.AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginUseCase, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

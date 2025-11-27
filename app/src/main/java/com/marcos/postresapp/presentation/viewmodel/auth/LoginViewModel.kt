package com.marcos.postresapp.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.postresapp.domain.model.User
import com.marcos.postresapp.domain.usecase.auth.LoginUseCase
import com.marcos.postresapp.domain.repository.auth.AuthRepository
import com.marcos.postresapp.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            
            val result = loginUseCase(username, password)
            
            _loginState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(
                    message = result.exceptionOrNull()?.message ?: "Error desconocido",
                    exception = result.exceptionOrNull()
                )
            }
        }
    }

    fun getUserRoles(): List<String> {
        return authRepository.getUserRoles()
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}

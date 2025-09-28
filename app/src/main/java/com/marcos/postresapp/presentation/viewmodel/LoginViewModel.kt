package com.marcos.postresapp.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.postresapp.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch
class LoginViewModel(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    fun login(
        username: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                onSuccess("Bienvenido ${response.user.nombres}")
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }
}
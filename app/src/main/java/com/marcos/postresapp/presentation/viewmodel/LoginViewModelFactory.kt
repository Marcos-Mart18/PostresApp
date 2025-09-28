package com.marcos.postresapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.repository.AuthRepositoryImpl

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    // Usamos el context para crear PrefsManager y NetworkClient
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val prefsManager = PrefsManager(context)
            val retrofit = NetworkClient.create(prefsManager)  // Creamos Retrofit con PrefsManager
            val api = retrofit.create(AuthApiService::class.java)  // Creamos AuthApiService con Retrofit
            val authRepository = AuthRepositoryImpl(api, prefsManager)  // Pasamos AuthApiService y PrefsManager
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.marcos.postresapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcos.postresapp.data.repository.CategoriaRepositoryImpl

class CategoriaViewModelFactory(
    private val repository: CategoriaRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

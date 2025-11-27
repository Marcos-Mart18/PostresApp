package com.marcos.postresapp.presentation.viewmodel.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marcos.postresapp.domain.usecase.categoria.ActualizarCategoriaUseCase
import com.marcos.postresapp.domain.usecase.categoria.CrearCategoriaUseCase
import com.marcos.postresapp.domain.usecase.categoria.GetCategoriasUseCase

class CategoriaViewModelFactory(
    private val getCategoriasUseCase: GetCategoriasUseCase,
    private val crearCategoriaUseCase: CrearCategoriaUseCase,
    private val actualizarCategoriaUseCase: ActualizarCategoriaUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriaViewModel(
                getCategoriasUseCase,
                crearCategoriaUseCase,
                actualizarCategoriaUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

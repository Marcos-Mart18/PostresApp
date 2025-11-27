package com.marcos.postresapp.presentation.viewmodel.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.usecase.categoria.ActualizarCategoriaUseCase
import com.marcos.postresapp.domain.usecase.categoria.CrearCategoriaUseCase
import com.marcos.postresapp.domain.usecase.categoria.GetCategoriasUseCase
import com.marcos.postresapp.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriaViewModel(
    private val getCategoriasUseCase: GetCategoriasUseCase,
    private val crearCategoriaUseCase: CrearCategoriaUseCase,
    private val actualizarCategoriaUseCase: ActualizarCategoriaUseCase
) : ViewModel() {

    private val _categoriasState = MutableStateFlow<UiState<List<Categoria>>>(UiState.Idle)
    val categoriasState: StateFlow<UiState<List<Categoria>>> = _categoriasState

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operacionState: StateFlow<UiState<String>> = _operacionState

    fun getCategorias() {
        viewModelScope.launch {
            _categoriasState.value = UiState.Loading
            getCategoriasUseCase().fold(
                onSuccess = { categorias ->
                    _categoriasState.value = UiState.Success(categorias)
                },
                onFailure = { error ->
                    _categoriasState.value = UiState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun crearCategoria(nombre: String) {
        viewModelScope.launch {
            _operacionState.value = UiState.Loading
            crearCategoriaUseCase(nombre).fold(
                onSuccess = {
                    _operacionState.value = UiState.Success("Categoría creada exitosamente")
                    getCategorias()
                },
                onFailure = { error ->
                    _operacionState.value = UiState.Error(error.message ?: "Error al crear categoría")
                }
            )
        }
    }

    fun actualizarCategoria(id: Int, nombre: String) {
        viewModelScope.launch {
            _operacionState.value = UiState.Loading
            actualizarCategoriaUseCase(id, nombre).fold(
                onSuccess = {
                    _operacionState.value = UiState.Success("Categoría actualizada exitosamente")
                    getCategorias()
                },
                onFailure = { error ->
                    _operacionState.value = UiState.Error(error.message ?: "Error al actualizar categoría")
                }
            )
        }
    }

    fun resetOperacionState() {
        _operacionState.value = UiState.Idle
    }
}

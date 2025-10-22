package com.marcos.postresapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.postresapp.data.repository.CategoriaRepositoryImpl
import com.marcos.postresapp.domain.model.Categoria
import kotlinx.coroutines.launch

class CategoriaViewModel(
    private val repository: CategoriaRepositoryImpl
) : ViewModel() {

    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> = _categorias

    private val _operationResult = MutableLiveData<Result<String>>()
    val operationResult: LiveData<Result<String>> = _operationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCategorias() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.getAllCategorias()
                _categorias.value = result
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCategoria(nombre: String) {
        if (nombre.isBlank()) {
            _operationResult.value = Result.failure(Exception("El nombre no puede estar vacío"))
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.createCategoria(nombre)
                _operationResult.value = Result.success("Categoría creada exitosamente")
                loadCategorias()
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategoria(id: Int, nombre: String) {
        if (nombre.isBlank()) {
            _operationResult.value = Result.failure(Exception("El nombre no puede estar vacío"))
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateCategoria(id, nombre)
                _operationResult.value = Result.success("Categoría actualizada exitosamente")
                loadCategorias()
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategoria(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteCategoria(id)
                _operationResult.value = Result.success("Categoría eliminada exitosamente")
                loadCategorias()
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

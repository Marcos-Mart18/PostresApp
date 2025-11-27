package com.marcos.postresapp.data.repository.categoria

import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.dto.categoria.CrearCategoriaDto
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val categoriaApiService: CategoriaApiService
) : CategoriaRepository {
    
    override suspend fun getCategorias(): Result<List<Categoria>> {
        return try {
            val response = categoriaApiService.getCategorias()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun crearCategoria(nombre: String): Result<Categoria> {
        return try {
            val request = CrearCategoriaDto(nombre)
            val response = categoriaApiService.crearCategoria(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun actualizarCategoria(id: Int, nombre: String): Result<Categoria> {
        return try {
            val request = CrearCategoriaDto(nombre)
            val response = categoriaApiService.actualizarCategoria(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

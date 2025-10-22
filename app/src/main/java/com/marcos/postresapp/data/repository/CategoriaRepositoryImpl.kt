package com.marcos.postresapp.data.repository

import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.domain.dto.CategoriaDTO
import com.marcos.postresapp.domain.model.Categoria

class CategoriaRepositoryImpl(
    private val api: CategoriaApiService
) {
    suspend fun getAllCategorias(): List<Categoria> {
        return api.getCategorias()
    }
    
    suspend fun createCategoria(nombre: String): Categoria {
        return api.createCategoria(CategoriaDTO(nombre))
    }
    
    suspend fun updateCategoria(id: Int, nombre: String): Categoria {
        return api.updateCategoria(id, CategoriaDTO(nombre))
    }
    
    suspend fun deleteCategoria(id: Int) {
        api.deleteCategoria(id)
    }
}

package com.marcos.postresapp.domain.repository

import com.marcos.postresapp.domain.model.Categoria

interface CategoriaRepository {
    suspend fun getCategorias(): Result<List<Categoria>>
    suspend fun crearCategoria(nombre: String): Result<Categoria>
    suspend fun actualizarCategoria(id: Int, nombre: String): Result<Categoria>
}

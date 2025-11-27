package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.data.remote.dto.categoria.CrearCategoriaDto
import com.marcos.postresapp.domain.model.Categoria
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CategoriaApiService {
    @GET("api/v1/categorias")
    suspend fun getCategorias(): List<Categoria>
    
    @POST("api/v1/categorias")
    suspend fun crearCategoria(@Body request: CrearCategoriaDto): Categoria
    
    @retrofit2.http.PUT("api/v1/categorias/{id}")
    suspend fun actualizarCategoria(
        @retrofit2.http.Path("id") id: Int,
        @Body request: CrearCategoriaDto
    ): Categoria
}
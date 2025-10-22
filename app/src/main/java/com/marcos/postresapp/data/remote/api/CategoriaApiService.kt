package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.domain.dto.CategoriaDTO
import com.marcos.postresapp.domain.model.Categoria
import retrofit2.http.*

interface CategoriaApiService {
    @GET("api/v1/categorias")
    suspend fun getCategorias(): List<Categoria>
    
    @POST("api/v1/categorias")
    suspend fun createCategoria(@Body categoriaDTO: CategoriaDTO): Categoria
    
    @PUT("api/v1/categorias/{id}")
    suspend fun updateCategoria(
        @Path("id") id: Int,
        @Body categoriaDTO: CategoriaDTO
    ): Categoria
    
    @DELETE("api/v1/categorias/{id}")
    suspend fun deleteCategoria(@Path("id") id: Int)
}

package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.domain.model.Categoria
import retrofit2.http.GET

interface CategoriaApiService {
    @GET("api/v1/categorias")
    suspend fun getCategorias(
    ): List<Categoria>
}
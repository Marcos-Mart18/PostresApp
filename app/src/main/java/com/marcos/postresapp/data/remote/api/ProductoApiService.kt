package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.domain.model.Producto
import retrofit2.http.GET

interface ProductoApiService {
    @GET("api/v1/productos")
    suspend fun getProductos(
    ): List<Producto>
}
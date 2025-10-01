package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.domain.dto.ProductoDTO
import com.marcos.postresapp.domain.model.Producto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductoApiService {
    @GET("api/v1/productos")
    suspend fun getProductos(
    ): List<Producto>

    @Multipart
    @POST("api/v1/productos/createWithImage")
    suspend fun createProductoWithImage(
        @Part("producto") producto: RequestBody, // Enviamos el objeto ProductoDTO como RequestBody
        @Part file: MultipartBody.Part  // Enviamos el archivo como Multipart
    ): Producto
}
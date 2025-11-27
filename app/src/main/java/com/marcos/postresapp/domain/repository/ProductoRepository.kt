package com.marcos.postresapp.domain.repository

import com.marcos.postresapp.domain.model.Producto
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProductoRepository {
    suspend fun getProductos(): Result<List<Producto>>
    suspend fun getProductoById(id: Long): Result<Producto>
    suspend fun createProductoWithImage(producto: RequestBody, image: MultipartBody.Part): Result<Unit>
    suspend fun deleteProducto(id: Long): Result<Unit>
}

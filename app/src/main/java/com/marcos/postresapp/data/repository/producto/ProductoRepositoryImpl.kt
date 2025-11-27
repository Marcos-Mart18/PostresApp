package com.marcos.postresapp.data.repository.producto

import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.domain.model.Producto
import com.marcos.postresapp.domain.repository.ProductoRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductoRepositoryImpl(
    private val productoApiService: ProductoApiService
) : ProductoRepository {
    
    override suspend fun getProductos(): Result<List<Producto>> {
        return try {
            val response = productoApiService.getProductos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProductoById(id: Long): Result<Producto> {
        return try {
            // Como no existe el endpoint, buscamos en la lista
            val productos = productoApiService.getProductos()
            val producto = productos.find { it.idProducto == id }
            if (producto != null) {
                Result.success(producto)
            } else {
                Result.failure(Exception("Producto no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createProductoWithImage(producto: RequestBody, image: MultipartBody.Part): Result<Unit> {
        return try {
            productoApiService.createProductoWithImage(producto, image)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteProducto(id: Long): Result<Unit> {
        return try {
            val response = productoApiService.deleteProducto(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar producto"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

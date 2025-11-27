package com.marcos.postresapp.data.remote.dto.producto

data class ProductoDto(
    val idProducto: Long?,
    val nombre: String,
    val fotoUrl: String?,
    val precio: Double,
    val descripcion: String,
    val isActive: Char = 'A',
    val categoria: CategoriaDto?
)

data class CategoriaDto(
    val idCategoria: Int,
    val nombre: String,
    val isActive: Char = 'A'
)

data class ProductoRequestDto(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val idCategoria: Int
)

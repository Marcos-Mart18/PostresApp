package com.marcos.postresapp.domain.model

data class Producto(
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    val fotoUrl: String?,
    val descripcion: String,
    val categoria: Categoria
)

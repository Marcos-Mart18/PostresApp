package com.marcos.postresapp.domain.model

// Modelo de dominio - representa un producto en la lógica de negocio
data class Producto(
    val idProducto: Long,
    val nombre: String,
    val precio: Double,
    val fotoUrl: String?,
    val descripcion: String,
    val categoria: Categoria?
)

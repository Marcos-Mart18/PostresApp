package com.marcos.postresapp.domain.dto

data class ProductoDTO(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val idCategoria: Int
)

package com.marcos.postresapp.data.mapper.producto

import com.marcos.postresapp.data.remote.dto.producto.CategoriaDto
import com.marcos.postresapp.data.remote.dto.producto.ProductoDto
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.model.Producto

fun ProductoDto.toDomain(): Producto? {
    // Si no tiene ID, no es un producto válido del servidor
    val id = idProducto ?: return null
    
    return Producto(
        idProducto = id,
        nombre = nombre,
        precio = precio,
        fotoUrl = fotoUrl,
        descripcion = descripcion,
        categoria = categoria?.toDomain()
    )
}

fun CategoriaDto.toDomain(): Categoria {
    return Categoria(
        idCategoria = idCategoria,
        nombre = nombre
    )
}

fun List<ProductoDto>.toDomainList(): List<Producto> {
    return this.mapNotNull { it.toDomain() }
}

fun List<CategoriaDto>.toCategoriaDomainList(): List<Categoria> {
    return this.map { it.toDomain() }
}

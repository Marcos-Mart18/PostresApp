package com.marcos.postresapp.domain.usecase.categoria

import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.repository.CategoriaRepository

class ActualizarCategoriaUseCase(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(id: Int, nombre: String): Result<Categoria> {
        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        return categoriaRepository.actualizarCategoria(id, nombre)
    }
}

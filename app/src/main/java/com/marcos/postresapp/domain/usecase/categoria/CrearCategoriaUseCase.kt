package com.marcos.postresapp.domain.usecase.categoria

import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.repository.CategoriaRepository

class CrearCategoriaUseCase(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(nombre: String): Result<Categoria> {
        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        return categoriaRepository.crearCategoria(nombre)
    }
}

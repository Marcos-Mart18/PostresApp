package com.marcos.postresapp.domain.usecase.categoria

import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.repository.CategoriaRepository

class GetCategoriasUseCase(
    private val categoriaRepository: CategoriaRepository
) {
    suspend operator fun invoke(): Result<List<Categoria>> {
        return categoriaRepository.getCategorias()
    }
}

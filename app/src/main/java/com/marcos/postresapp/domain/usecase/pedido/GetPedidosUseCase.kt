package com.marcos.postresapp.domain.usecase.pedido

import com.marcos.postresapp.domain.model.Pedido
import com.marcos.postresapp.domain.repository.pedido.PedidoRepository

class GetPedidosUseCase(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(): Result<List<Pedido>> {
        return repository.getPedidos()
    }
}

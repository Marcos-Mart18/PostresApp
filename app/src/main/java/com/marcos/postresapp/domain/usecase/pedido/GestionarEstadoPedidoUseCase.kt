package com.marcos.postresapp.domain.usecase.pedido

import com.marcos.postresapp.domain.model.Pedido
import com.marcos.postresapp.domain.repository.pedido.PedidoRepository

class AceptarPedidoUseCase(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(id: Long): Result<Pedido> {
        return repository.aceptarPedido(id)
    }
}

class MarcarEnPreparacionUseCase(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(id: Long): Result<Pedido> {
        return repository.marcarEnPreparacion(id)
    }
}

class MarcarListoUseCase(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(id: Long): Result<Pedido> {
        return repository.marcarListoParaEntrega(id)
    }
}

class AsignarRepartidorUseCase(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(idPedido: Long, idRepartidor: Long): Result<Pedido> {
        return repository.asignarRepartidor(idPedido, idRepartidor)
    }
}

class CancelarPedidoUseCase(
    private val repository: PedidoRepository
) {
    suspend operator fun invoke(id: Long): Result<Pedido> {
        return repository.cancelarPedido(id)
    }
}

package com.marcos.postresapp.domain.repository.pedido

import com.marcos.postresapp.domain.model.Pedido

interface PedidoRepository {
    suspend fun getPedidos(): Result<List<Pedido>>
    suspend fun getPedidoById(id: Long): Result<Pedido>
    suspend fun aceptarPedido(id: Long): Result<Pedido>
    suspend fun marcarEnPreparacion(id: Long): Result<Pedido>
    suspend fun marcarListoParaEntrega(id: Long): Result<Pedido>
    suspend fun asignarRepartidor(idPedido: Long, idRepartidor: Long): Result<Pedido>
    suspend fun cancelarPedido(id: Long): Result<Pedido>
}

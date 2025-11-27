package com.marcos.postresapp.data.repository.pedido

import com.marcos.postresapp.data.mapper.pedido.toDomain
import com.marcos.postresapp.data.mapper.pedido.toDomainList
import com.marcos.postresapp.data.remote.api.PedidoApiService
import com.marcos.postresapp.domain.model.Pedido
import com.marcos.postresapp.domain.repository.pedido.PedidoRepository

class PedidoRepositoryImpl(
    private val pedidoApiService: PedidoApiService
) : PedidoRepository {

    override suspend fun getPedidos(): Result<List<Pedido>> {
        return try {
            val response = pedidoApiService.getPedidos()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPedidoById(id: Long): Result<Pedido> {
        return try {
            val response = pedidoApiService.getPedidoById(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun aceptarPedido(id: Long): Result<Pedido> {
        return try {
            val response = pedidoApiService.aceptarPedido(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarEnPreparacion(id: Long): Result<Pedido> {
        return try {
            val response = pedidoApiService.marcarEnPreparacion(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun marcarListoParaEntrega(id: Long): Result<Pedido> {
        return try {
            val response = pedidoApiService.marcarListoParaEntrega(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun asignarRepartidor(idPedido: Long, idRepartidor: Long): Result<Pedido> {
        return try {
            val response = pedidoApiService.asignarRepartidor(idPedido, idRepartidor)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarPedido(id: Long): Result<Pedido> {
        return try {
            val response = pedidoApiService.cancelarPedido(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

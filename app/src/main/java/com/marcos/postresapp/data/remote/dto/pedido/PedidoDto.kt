package com.marcos.postresapp.data.remote.dto.pedido

data class PedidoDto(
    val idPedido: Long,
    val fechaEntrega: String?,
    val horaEntrega: String?,
    val fechaPedido: String?,
    val total: Double,
    val numOrden: String?,
    val direccion: String?,
    val usuario: UsuarioDto?,
    val repartidor: RepartidorDto?,
    val estado: EstadoDto?
)

data class UsuarioDto(
    val idUsuario: Long,
    val username: String
)

data class RepartidorDto(
    val idRepartidor: Long,
    val codigo: String
)

data class EstadoDto(
    val idEstado: Long,
    val nombre: String
)

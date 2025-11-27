package com.marcos.postresapp.domain.model

data class Pedido(
    val idPedido: Long,
    val fechaEntrega: String?,
    val horaEntrega: String?,
    val fechaPedido: String?,
    val total: Double,
    val numOrden: String?,
    val direccion: String?,
    val usuario: Usuario?,
    val repartidor: Repartidor?,
    val estado: Estado?
)

data class Usuario(
    val idUsuario: Long,
    val username: String
)

data class Repartidor(
    val idRepartidor: Long,
    val codigo: String
)

data class Estado(
    val idEstado: Long,
    val nombre: String
)

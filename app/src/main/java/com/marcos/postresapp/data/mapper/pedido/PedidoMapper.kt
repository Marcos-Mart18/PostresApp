package com.marcos.postresapp.data.mapper.pedido

import com.marcos.postresapp.data.remote.dto.pedido.*
import com.marcos.postresapp.domain.model.*

fun PedidoDto.toDomain(): Pedido {
    return Pedido(
        idPedido = idPedido,
        fechaEntrega = fechaEntrega,
        horaEntrega = horaEntrega,
        fechaPedido = fechaPedido,
        total = total,
        numOrden = numOrden,
        direccion = direccion,
        usuario = usuario?.toDomain(),
        repartidor = repartidor?.toDomain(),
        estado = estado?.toDomain()
    )
}

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        idUsuario = idUsuario,
        username = username
    )
}

fun RepartidorDto.toDomain(): Repartidor {
    return Repartidor(
        idRepartidor = idRepartidor,
        codigo = codigo
    )
}

fun EstadoDto.toDomain(): Estado {
    return Estado(
        idEstado = idEstado,
        nombre = nombre
    )
}

fun List<PedidoDto>.toDomainList(): List<Pedido> {
    return this.map { it.toDomain() }
}

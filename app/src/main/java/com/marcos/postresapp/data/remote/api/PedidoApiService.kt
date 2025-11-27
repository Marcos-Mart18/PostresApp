package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.data.remote.dto.pedido.PedidoDto
import retrofit2.http.*

interface PedidoApiService {
    
    @GET("api/v1/pedidos")
    suspend fun getPedidos(): List<PedidoDto>
    
    @GET("api/v1/pedidos/{id}")
    suspend fun getPedidoById(@Path("id") id: Long): PedidoDto
    
    @PUT("api/v1/pedidos/{id}/aceptar")
    suspend fun aceptarPedido(@Path("id") id: Long): PedidoDto
    
    @PUT("api/v1/pedidos/{id}/en-preparacion")
    suspend fun marcarEnPreparacion(@Path("id") id: Long): PedidoDto
    
    @PUT("api/v1/pedidos/{id}/listo-para-entrega")
    suspend fun marcarListoParaEntrega(@Path("id") id: Long): PedidoDto
    
    @PUT("api/v1/pedidos/{id}/asignar/{idRepartidor}")
    suspend fun asignarRepartidor(
        @Path("id") id: Long,
        @Path("idRepartidor") idRepartidor: Long
    ): PedidoDto
    
    @PUT("api/v1/pedidos/{id}/cancelar")
    suspend fun cancelarPedido(@Path("id") id: Long): PedidoDto
}

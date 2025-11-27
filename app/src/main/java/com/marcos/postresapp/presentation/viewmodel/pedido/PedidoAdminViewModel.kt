package com.marcos.postresapp.presentation.viewmodel.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcos.postresapp.domain.model.Pedido
import com.marcos.postresapp.domain.usecase.pedido.*
import com.marcos.postresapp.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PedidoAdminViewModel(
    private val getPedidosUseCase: GetPedidosUseCase,
    private val aceptarPedidoUseCase: AceptarPedidoUseCase,
    private val marcarEnPreparacionUseCase: MarcarEnPreparacionUseCase,
    private val marcarListoUseCase: MarcarListoUseCase,
    private val asignarRepartidorUseCase: AsignarRepartidorUseCase,
    private val cancelarPedidoUseCase: CancelarPedidoUseCase
) : ViewModel() {

    private val _pedidosState = MutableStateFlow<UiState<List<Pedido>>>(UiState.Idle)
    val pedidosState: StateFlow<UiState<List<Pedido>>> = _pedidosState.asStateFlow()

    private val _accionState = MutableStateFlow<UiState<Pedido>>(UiState.Idle)
    val accionState: StateFlow<UiState<Pedido>> = _accionState.asStateFlow()

    fun cargarPedidos() {
        viewModelScope.launch {
            _pedidosState.value = UiState.Loading
            
            val result = getPedidosUseCase()
            
            _pedidosState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(
                    message = result.exceptionOrNull()?.message ?: "Error al cargar pedidos"
                )
            }
        }
    }

    fun aceptarPedido(id: Long) {
        ejecutarAccion { aceptarPedidoUseCase(id) }
    }

    fun marcarEnPreparacion(id: Long) {
        ejecutarAccion { marcarEnPreparacionUseCase(id) }
    }

    fun marcarListo(id: Long) {
        ejecutarAccion { marcarListoUseCase(id) }
    }

    fun asignarRepartidor(idPedido: Long, idRepartidor: Long) {
        ejecutarAccion { asignarRepartidorUseCase(idPedido, idRepartidor) }
    }

    fun cancelarPedido(id: Long) {
        ejecutarAccion { cancelarPedidoUseCase(id) }
    }

    private fun ejecutarAccion(accion: suspend () -> Result<Pedido>) {
        viewModelScope.launch {
            _accionState.value = UiState.Loading
            
            val result = accion()
            
            _accionState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(
                    message = result.exceptionOrNull()?.message ?: "Error en la operación"
                )
            }
            
            // Recargar pedidos después de cualquier acción
            if (result.isSuccess) {
                cargarPedidos()
            }
        }
    }

    fun resetAccionState() {
        _accionState.value = UiState.Idle
    }
}

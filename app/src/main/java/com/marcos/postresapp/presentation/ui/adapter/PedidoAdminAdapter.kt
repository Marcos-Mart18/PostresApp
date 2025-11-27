package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.R
import com.marcos.postresapp.domain.model.Pedido

class PedidoAdminAdapter(
    private var pedidos: List<Pedido>,
    private val onAceptar: (Pedido) -> Unit,
    private val onEnPreparacion: (Pedido) -> Unit,
    private val onListo: (Pedido) -> Unit,
    private val onCancelar: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidoAdminAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumOrden: TextView = view.findViewById(R.id.tvNumOrden)
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        val btnEnPreparacion: Button = view.findViewById(R.id.btnEnPreparacion)
        val btnListo: Button = view.findViewById(R.id.btnListo)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_admin, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        
        holder.tvNumOrden.text = "Pedido #${pedido.numOrden ?: pedido.idPedido}"
        holder.tvCliente.text = "Cliente: ${pedido.usuario?.username ?: "N/A"}"
        holder.tvEstado.text = "Estado: ${pedido.estado?.nombre ?: "N/A"}"
        holder.tvTotal.text = "Total: S/. ${String.format("%.2f", pedido.total)}"
        holder.tvFecha.text = "${pedido.fechaEntrega ?: ""} ${pedido.horaEntrega ?: ""}"

        // Mostrar botones según el estado
        val estado = pedido.estado?.nombre ?: ""
        holder.btnAceptar.visibility = if (estado == "PENDIENTE") View.VISIBLE else View.GONE
        holder.btnEnPreparacion.visibility = if (estado == "ACEPTADO") View.VISIBLE else View.GONE
        holder.btnListo.visibility = if (estado == "EN_PREPARACION") View.VISIBLE else View.GONE
        holder.btnCancelar.visibility = if (estado !in listOf("ENTREGADO", "CANCELADO")) View.VISIBLE else View.GONE

        holder.btnAceptar.setOnClickListener { onAceptar(pedido) }
        holder.btnEnPreparacion.setOnClickListener { onEnPreparacion(pedido) }
        holder.btnListo.setOnClickListener { onListo(pedido) }
        holder.btnCancelar.setOnClickListener { onCancelar(pedido) }
    }

    override fun getItemCount() = pedidos.size

    fun actualizarLista(nuevosPedidos: List<Pedido>) {
        pedidos = nuevosPedidos
        notifyDataSetChanged()
    }
}

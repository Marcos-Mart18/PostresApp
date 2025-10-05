package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marcos.postresapp.R
import com.marcos.postresapp.domain.model.Producto

class ProductoAdapterAdmin(
    private val productos: MutableList<Producto>,
    private val onLongPress: (Producto, Int) -> Unit,           // long press -> eliminar / opciones
    private val onEditClick: ((Producto, Int) -> Unit)? = null  // opcional: click en editar
) : RecyclerView.Adapter<ProductoAdapterAdmin.ProductoViewHolder>() {

    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto_admin)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre_admin)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion_admin)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio_admin)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_admin, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion
        holder.tvPrecio.text = "$${producto.precio ?: 0.0}"

        Glide.with(holder.imgProducto.context)
            .load(producto.fotoUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .into(holder.imgProducto)

        // Long press en toda la card
        holder.itemView.setOnLongClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
            onLongPress(producto, holder.bindingAdapterPosition)
            true
        }

        // Click en editar (opcional)
        holder.btnEdit.setOnClickListener {
            onEditClick?.invoke(producto, holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int = productos.size

    /** Reemplaza la lista completa */
    fun actualizarLista(nuevaLista: List<Producto>) {
        productos.clear()
        productos.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    /** Elimina un item por posición (útil tras delete en backend) */
    fun removeAt(position: Int) {
        if (position in 0 until productos.size) {
            productos.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

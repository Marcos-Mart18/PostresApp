package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.marcos.postresapp.R
import com.marcos.postresapp.domain.model.Producto

class ProductoAdapter(private var productos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto)
        val btnAgregar: ImageView = view.findViewById(R.id.btnAgregar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion
        holder.tvPrecio.text = "S/ ${producto.precio}"

        if (!producto.fotoUrl.isNullOrEmpty()) {
            Glide.with(holder.imgProducto.context)
                .load(producto.fotoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.imgProducto)
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_placeholder) // si es null
        }

        holder.btnAgregar.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "${producto.nombre} agregado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount() = productos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}

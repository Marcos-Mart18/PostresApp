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

class ProductoAdapterAdmin(private var productos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapterAdmin.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto_admin)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre_admin)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion_admin)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio_admin)
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        // Infla el layout de cada producto
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_admin, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        // Asignar los datos a los elementos de la vista
        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion
        holder.tvPrecio.text = "$${producto.precio}"

        Glide.with(holder.imgProducto.context).load(producto.fotoUrl).into(holder.imgProducto)

        holder.btnEdit.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = productos.size

    // Método para actualizar la lista de productos
    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}

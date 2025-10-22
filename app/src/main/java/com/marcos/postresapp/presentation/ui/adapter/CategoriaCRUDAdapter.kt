package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.marcos.postresapp.R
import com.marcos.postresapp.domain.model.Categoria

class CategoriaCRUDAdapter(
    private var categorias: List<Categoria>,
    private val onEditClick: (Categoria) -> Unit,
    private val onDeleteClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaCRUDAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreCategoria: TextView = view.findViewById(R.id.tvNombreCategoria)
        val btnEditar: MaterialButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: MaterialButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_crud, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.tvNombreCategoria.text = categoria.nombre

        holder.btnEditar.setOnClickListener {
            onEditClick(categoria)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(categoria)
        }
    }

    override fun getItemCount() = categorias.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        categorias = nuevaLista
        notifyDataSetChanged()
    }
}

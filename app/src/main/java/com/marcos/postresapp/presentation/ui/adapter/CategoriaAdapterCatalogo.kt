package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.R
import com.marcos.postresapp.domain.model.Categoria

class CategoriaAdapterCatalogo(
    private var categorias: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapterCatalogo.CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria_chip, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    override fun getItemCount(): Int = categorias.size

    fun actualizarLista(nuevasCategorias: List<Categoria>) {
        categorias = nuevasCategorias
        notifyDataSetChanged()
    }

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvCategoriaNombre)

        fun bind(categoria: Categoria) {
            tvNombre.text = categoria.nombre
            itemView.setOnClickListener {
                onCategoriaClick(categoria)
            }
        }
    }
}

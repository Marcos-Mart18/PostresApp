package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.R
import com.marcos.postresapp.domain.model.Categoria

class CategoriaAdapter(
    private var categorias: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val layout: LinearLayout = view.findViewById(R.id.layoutCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[holder.adapterPosition]
        holder.tvCategoria.text = categoria.nombre

        // Cambiar estilo según si está seleccionada
        if (holder.adapterPosition == selectedPosition) {
            holder.layout.setBackgroundResource(R.drawable.bg_chip_selector)
        } else {
            holder.layout.setBackgroundResource(R.drawable.bg_chip_outline)
        }

        holder.layout.setOnClickListener {
            val prevPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(prevPosition)
            notifyItemChanged(selectedPosition)
            onCategoriaClick(categoria)
        }
    }

    override fun getItemCount() = categorias.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        categorias = nuevaLista
        notifyDataSetChanged()
    }
}

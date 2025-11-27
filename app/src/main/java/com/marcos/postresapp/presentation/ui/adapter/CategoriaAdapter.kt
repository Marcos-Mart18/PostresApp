package com.marcos.postresapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.databinding.ItemCategoriaBinding
import com.marcos.postresapp.domain.model.Categoria

class CategoriaAdapter(
    private val onEditClick: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriaAdapter.CategoriaViewHolder>(CategoriaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoriaViewHolder(binding, onEditClick)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoriaViewHolder(
        private val binding: ItemCategoriaBinding,
        private val onEditClick: (Categoria) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoria: Categoria) {
            binding.tvNombreCategoria.text = categoria.nombre
            binding.tvIdCategoria.text = "ID: ${categoria.idCategoria}"
            
            binding.btnEditarCategoria.setOnClickListener {
                onEditClick(categoria)
            }
        }
    }

    private class CategoriaDiffCallback : DiffUtil.ItemCallback<Categoria>() {
        override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem.idCategoria == newItem.idCategoria
        }

        override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem == newItem
        }
    }
}

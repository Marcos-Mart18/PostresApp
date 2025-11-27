package com.marcos.postresapp.presentation.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.marcos.postresapp.domain.model.Categoria

class CategoriaSpinnerAdapter(
    context: Context,
    private val categorias: List<Categoria>
) : ArrayAdapter<Categoria>(context, android.R.layout.simple_spinner_item, categorias) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = categorias[position].nombre
        view.setTextColor(android.graphics.Color.BLACK)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = categorias[position].nombre
        view.setTextColor(android.graphics.Color.BLACK)
        return view
    }
}

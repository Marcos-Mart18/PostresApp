package com.marcos.postresapp.presentation.ui.activity.user

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.R
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapter
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapter
import kotlinx.coroutines.launch

class CatalogoActivity : AppCompatActivity() {
    private lateinit var rvProductos: RecyclerView
    private lateinit var rvCategorias: RecyclerView

    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var categoriaAdapter: CategoriaAdapter

    private val productApiService = NetworkClient.retrofit.create(ProductoApiService::class.java)
    private val categoriaApiService = NetworkClient.retrofit.create(CategoriaApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalogo)

        // RecyclerView de productos
        rvProductos = findViewById(R.id.rvProductos)
        rvProductos.layoutManager = GridLayoutManager(this, 2)
        productoAdapter = ProductoAdapter(emptyList())
        rvProductos.adapter = productoAdapter

        // RecyclerView de categorías
        rvCategorias = findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            filtrarPorCategoria(categoria)
        }
        rvCategorias.adapter = categoriaAdapter

        cargarProductos()
        cargarCategorias()
    }

    private fun cargarProductos() {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                productoAdapter.actualizarLista(productos)
            } catch (e: Exception) {
                Log.e("CatalogoActivity", "Error cargando productos", e)
                Toast.makeText(
                    this@CatalogoActivity,
                    "Error cargando productos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val categorias = categoriaApiService.getCategorias()
                categoriaAdapter.actualizarLista(categorias)
            } catch (e: Exception) {
                Log.e("CatalogoActivity", "Error cargando categorías", e)
                Toast.makeText(
                    this@CatalogoActivity,
                    "Error cargando categorías",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun filtrarPorCategoria(categoria: Categoria) {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                val filtrados =
                    productos.filter { it.categoria?.idCategoria == categoria.idCategoria }
                productoAdapter.actualizarLista(filtrados)
            } catch (e: Exception) {
                Log.e("CatalogoActivity", "Error filtrando productos", e)
                Toast.makeText(
                    this@CatalogoActivity,
                    "Error al filtrar productos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

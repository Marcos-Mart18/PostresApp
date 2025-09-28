package com.marcos.postresapp.presentation.ui.activity.user

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.R
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapter
import kotlinx.coroutines.launch

class CatalogoActivity : AppCompatActivity() {
    private lateinit var rvProductos: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private val apiService = NetworkClient.retrofit.create(ProductoApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalogo)
        rvProductos = findViewById(R.id.rvProductos)
        rvProductos.layoutManager = GridLayoutManager(this, 2)

        productoAdapter = ProductoAdapter(emptyList())
        rvProductos.adapter = productoAdapter

        cargarProductos()
    }

    private fun cargarProductos() {
        lifecycleScope.launch {
            try {
                val productos = apiService.getProductos()
                productoAdapter.actualizarLista(productos)
            } catch (e: Exception) {
                android.util.Log.e("CatalogoActivity", "Error cargando productos", e)
                Toast.makeText(
                    this@CatalogoActivity,
                    "Error: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
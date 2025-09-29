package com.marcos.postresapp.presentation.ui.activity.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.marcos.postresapp.presentation.ui.adapter.ImageAdapter
import kotlinx.coroutines.launch
import androidx.viewpager2.widget.ViewPager2
import com.marcos.postresapp.domain.model.Producto

class CatalogoActivity : AppCompatActivity() {
    private lateinit var rvProductos: RecyclerView
    private lateinit var rvCategorias: RecyclerView
    private lateinit var viewPager: ViewPager2

    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var categoriaAdapter: CategoriaAdapter

    private val productApiService = NetworkClient.retrofit.create(ProductoApiService::class.java)
    private val categoriaApiService = NetworkClient.retrofit.create(CategoriaApiService::class.java)

    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0  // Página actual para el carrusel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalogo)

        // Inicializando RecyclerView de productos
        rvProductos = findViewById(R.id.rvProductos)
        rvProductos.layoutManager = GridLayoutManager(this, 2)
        productoAdapter = ProductoAdapter(emptyList())
        rvProductos.adapter = productoAdapter

        // Inicializando RecyclerView de categorías
        rvCategorias = findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            filtrarPorCategoria(categoria)
        }
        rvCategorias.adapter = categoriaAdapter

        // Inicializando el ViewPager2 para el carrusel de imágenes
        viewPager = findViewById(R.id.viewPager)

        cargarProductos()
        cargarCategorias()
    }

    // Cargar productos y actualizar el RecyclerView
    private fun cargarProductos() {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                productoAdapter.actualizarLista(productos)

                // Cargar las imágenes del carrusel
                cargarFotosCarrusel(productos)

            } catch (e: Exception) {
                Log.e("CatalogoActivity", "Error cargando productoszzzzzz", e)
                Toast.makeText(this@CatalogoActivity, "Error cargando productos", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para cargar las imágenes del carrusel
    private fun cargarFotosCarrusel(productos: List<Producto>) {
        // Extrae las URLs de las imágenes de los productos
        val imagenUrls = productos.mapNotNull { it.fotoUrl }  // Usar mapNotNull para eliminar nulos
        configurarCarrusel(imagenUrls)  // Configura el carrusel con las URLs de las imágenes
    }

    // Cargar categorías y actualizar el RecyclerView
    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val categorias = categoriaApiService.getCategorias()
                categoriaAdapter.actualizarLista(categorias)
            } catch (e: Exception) {
                Log.e("CatalogoActivity", "Error cargando categorías", e)
                Toast.makeText(this@CatalogoActivity, "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Filtrar productos por categoría
    private fun filtrarPorCategoria(categoria: Categoria) {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                val filtrados = productos.filter { it.categoria?.idCategoria == categoria.idCategoria }
                productoAdapter.actualizarLista(filtrados)
            } catch (e: Exception) {
                Log.e("CatalogoActivity", "Error filtrando productos", e)
                Toast.makeText(this@CatalogoActivity, "Error al filtrar productos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Configurar el carrusel con las URLs de las imágenes
    private fun configurarCarrusel(imagenUrls: List<String>) {
        val imageAdapter = ImageAdapter(imagenUrls)  // Crear un nuevo adaptador para el carrusel
        viewPager.adapter = imageAdapter  // Establecer el adaptador en el ViewPager2

        // Configurar el carrusel para que se desplace automáticamente cada 3 segundos
        iniciarCarrusel()
    }

    // Iniciar el carrusel para que se mueva automáticamente
    private fun iniciarCarrusel() {
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == viewPager.adapter?.itemCount?.minus(1)) {
                    currentPage = 0
                } else {
                    currentPage++
                }

                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 3000)  // Cambia la página cada 3 segundos
            }
        }

        handler.post(runnable)  // Inicia el desplazamiento automático
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)  // Detener el carrusel cuando la actividad se destruya
    }
}

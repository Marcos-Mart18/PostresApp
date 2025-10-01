package com.marcos.postresapp.presentation.ui.fragment.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.marcos.postresapp.R
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.data.repository.AuthRepositoryImpl
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.model.Producto
import com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapter
import com.marcos.postresapp.presentation.ui.adapter.ImageAdapter
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapter
import kotlinx.coroutines.launch

class CatalogoUserFragment : Fragment() {

    private lateinit var rvProductos: RecyclerView
    private lateinit var rvCategorias: RecyclerView
    private lateinit var viewPager: ViewPager2
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var categoriaAdapter: CategoriaAdapter

    private lateinit var productApiService: ProductoApiService
    private lateinit var categoriaApiService: CategoriaApiService

    private var currentPage = 0  // Página actual para el carrusel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_catalogo_user, container, false)

        // 🔑 Inicializar PrefsManager y AuthRepository
        val prefsManager = PrefsManager(requireContext())
        val authApiService = NetworkClient.createBasic().create(AuthApiService::class.java)
        val authRepository = AuthRepositoryImpl(authApiService, prefsManager)

        // Retrofit con interceptor
        val retrofitProtected = NetworkClient.create(prefsManager, authRepository)
        productApiService = retrofitProtected.create(ProductoApiService::class.java)
        categoriaApiService = retrofitProtected.create(CategoriaApiService::class.java)

        // Inicializando RecyclerView de productos
        rvProductos = rootView.findViewById(R.id.rvProductos)
        rvProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        productoAdapter = ProductoAdapter(emptyList())
        rvProductos.adapter = productoAdapter

        // Inicializando RecyclerView de categorías
        rvCategorias = rootView.findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            filtrarPorCategoria(categoria)
        }
        rvCategorias.adapter = categoriaAdapter

        // Inicializando el ViewPager2 para el carrusel de imágenes
        viewPager = rootView.findViewById(R.id.viewPager)

        // Configuración del chip "Todos"
        val chipTodos: LinearLayout = rootView.findViewById(R.id.chipTodos)
        chipTodos.setOnClickListener {
            mostrarTodosLosProductos()
        }

        // Cargar productos y categorías
        cargarProductos()
        cargarCategorias()

        return rootView
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
                Log.e("CatalogoUserFragment", "Error cargando productos", e)
                Toast.makeText(requireContext(), "Error cargando productos", Toast.LENGTH_LONG).show()
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
                Log.e("CatalogoUserFragment", "Error cargando categorías", e)
                Toast.makeText(requireContext(), "Error cargando categorías", Toast.LENGTH_SHORT).show()
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
                Log.e("CatalogoUserFragment", "Error filtrando productos", e)
                Toast.makeText(requireContext(), "Error al filtrar productos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mostrar todos los productos (sin filtro)
    private fun mostrarTodosLosProductos() {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                productoAdapter.actualizarLista(productos)
            } catch (e: Exception) {
                Log.e("CatalogoUserFragment", "Error cargando productos", e)
                Toast.makeText(requireContext(), "Error cargando productos", Toast.LENGTH_LONG).show()
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
                Handler(Looper.getMainLooper()).postDelayed(this, 3000)  // Cambia la página cada 3 segundos
            }
        }

        Handler(Looper.getMainLooper()).post(runnable)  // Inicia el desplazamiento automático
    }
}

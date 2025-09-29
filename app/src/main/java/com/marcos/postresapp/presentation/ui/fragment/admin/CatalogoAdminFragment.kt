package com.marcos.postresapp.presentation.ui.fragment.admin

import android.os.Bundle
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
import com.marcos.postresapp.R
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapter
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapter
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapterAdmin
import kotlinx.coroutines.launch

class CatalogoAdminFragment : Fragment() {
    private lateinit var rvCategorias: RecyclerView
    private lateinit var productoAdapter: ProductoAdapterAdmin
    private lateinit var categoriaAdapter: CategoriaAdapter

    private val productApiService = NetworkClient.retrofit.create(ProductoApiService::class.java)
    private val categoriaApiService = NetworkClient.retrofit.create(CategoriaApiService::class.java)

    // Secciones de la vista para el formulario
    private lateinit var cardAgregar: View
    private lateinit var panelForm: View
    private lateinit var btnCrear: View

    private lateinit var btnCancel: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para el fragmento
        val rootView = inflater.inflate(R.layout.fragment_catalogo_admin, container, false)

        // Inicializa las vistas
        cardAgregar = rootView.findViewById(R.id.cardAgregar)
        panelForm = rootView.findViewById(R.id.panelForm)
        val rvProductos = rootView.findViewById<RecyclerView>(R.id.adProductos)
        btnCrear = rootView.findViewById(R.id.btnCrear)
        btnCancel = rootView.findViewById(R.id.btnCancel)

        // Establecer listeners de los botones
        cardAgregar.setOnClickListener {
            rvProductos.visibility = View.GONE
            panelForm.visibility = View.VISIBLE
        }

        btnCancel.setOnClickListener {
            panelForm.visibility = View.GONE
            rvProductos.visibility = View.VISIBLE
        }

        // Inicializando RecyclerView de productos
        rvProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        productoAdapter = ProductoAdapterAdmin(emptyList())
        rvProductos.adapter = productoAdapter

        // Inicializando RecyclerView de categorías
        rvCategorias = rootView.findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria ->
            filtrarPorCategoria(categoria)
        }
        rvCategorias.adapter = categoriaAdapter

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
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando productos", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Cargar categorías y actualizar el RecyclerView
    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val categorias = categoriaApiService.getCategorias()
                categoriaAdapter.actualizarLista(categorias)
            } catch (e: Exception) {
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
                Toast.makeText(requireContext(), "Error cargando productos", Toast.LENGTH_LONG).show()
            }
        }
    }
}

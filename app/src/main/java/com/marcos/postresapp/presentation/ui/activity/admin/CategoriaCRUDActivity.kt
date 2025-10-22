package com.marcos.postresapp.presentation.ui.activity.admin

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.marcos.postresapp.R
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.repository.AuthRepositoryImpl
import com.marcos.postresapp.data.repository.CategoriaRepositoryImpl
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.presentation.ui.adapter.CategoriaCRUDAdapter
import com.marcos.postresapp.presentation.viewmodel.CategoriaViewModel
import com.marcos.postresapp.presentation.viewmodel.CategoriaViewModelFactory

class CategoriaCRUDActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoriaViewModel
    private lateinit var adapter: CategoriaCRUDAdapter
    
    private lateinit var toolbar: MaterialToolbar
    private lateinit var etNombre: TextInputEditText
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnCancelar: MaterialButton
    private lateinit var tvFormTitle: TextView
    private lateinit var rvCategorias: RecyclerView
    private lateinit var progressBar: ProgressBar
    
    private var categoriaEnEdicion: Categoria? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria_crud_activity)

        initViews()
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        
        viewModel.loadCategorias()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        etNombre = findViewById(R.id.etNombre)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        tvFormTitle = findViewById(R.id.tvFormTitle)
        rvCategorias = findViewById(R.id.rvCategorias)
        progressBar = findViewById(R.id.progressBar)
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        val prefsManager = PrefsManager(this)
        val authApiService = NetworkClient.createBasic().create(AuthApiService::class.java)
        val authRepository = AuthRepositoryImpl(authApiService, prefsManager)
        
        val categoriaApiService = NetworkClient.create(prefsManager, authRepository)
            .create(CategoriaApiService::class.java)
        val categoriaRepository = CategoriaRepositoryImpl(categoriaApiService)
        
        val factory = CategoriaViewModelFactory(categoriaRepository)
        viewModel = ViewModelProvider(this, factory)[CategoriaViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = CategoriaCRUDAdapter(
            categorias = emptyList(),
            onEditClick = { categoria ->
                editarCategoria(categoria)
            },
            onDeleteClick = { categoria ->
                confirmarEliminacion(categoria)
            }
        )
        rvCategorias.adapter = adapter
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        
        btnGuardar.setOnClickListener {
            guardarCategoria()
        }
        
        btnCancelar.setOnClickListener {
            cancelarEdicion()
        }
    }

    private fun observeViewModel() {
        viewModel.categorias.observe(this) { categorias ->
            adapter.actualizarLista(categorias)
        }

        viewModel.operationResult.observe(this) { result ->
            result.onSuccess { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                limpiarFormulario()
            }
            result.onFailure { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnGuardar.isEnabled = !isLoading
        }
    }

    private fun guardarCategoria() {
        val nombre = etNombre.text.toString().trim()
        
        if (categoriaEnEdicion != null) {
            viewModel.updateCategoria(categoriaEnEdicion!!.idCategoria, nombre)
        } else {
            viewModel.createCategoria(nombre)
        }
    }

    private fun editarCategoria(categoria: Categoria) {
        categoriaEnEdicion = categoria
        tvFormTitle.text = "Editar Categoría"
        etNombre.setText(categoria.nombre)
        btnCancelar.visibility = View.VISIBLE
        btnGuardar.text = "Actualizar"
    }

    private fun confirmarEliminacion(categoria: Categoria) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de eliminar la categoría '${categoria.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteCategoria(categoria.idCategoria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cancelarEdicion() {
        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        categoriaEnEdicion = null
        etNombre.setText("")
        tvFormTitle.text = "Nueva Categoría"
        btnCancelar.visibility = View.GONE
        btnGuardar.text = "Guardar"
    }
}
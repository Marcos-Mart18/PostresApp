package com.marcos.postresapp.presentation.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.marcos.postresapp.R
import com.marcos.postresapp.databinding.FragmentCategoriaAdminBinding
import com.marcos.postresapp.di.ServiceLocator
import com.marcos.postresapp.presentation.state.UiState
import com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapter
import com.marcos.postresapp.presentation.viewmodel.categoria.CategoriaViewModel
import com.marcos.postresapp.presentation.viewmodel.categoria.CategoriaViewModelFactory
import kotlinx.coroutines.launch

class CategoriaAdminFragment : Fragment() {

    private var _binding: FragmentCategoriaAdminBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: CategoriaViewModel
    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriaAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.getCategorias()
    }

    private fun setupViewModel() {
        val factory = CategoriaViewModelFactory(
            ServiceLocator.provideGetCategoriasUseCase(requireContext()),
            ServiceLocator.provideCrearCategoriaUseCase(requireContext()),
            ServiceLocator.provideActualizarCategoriaUseCase(requireContext())
        )
        viewModel = ViewModelProvider(this, factory)[CategoriaViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = CategoriaAdapter(
            onEditClick = { categoria -> mostrarDialogoEditarCategoria(categoria) }
        )
        binding.recyclerCategorias.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategoriaAdminFragment.adapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriasState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerCategorias.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerCategorias.visibility = View.VISIBLE
                        adapter.submitList(state.data)
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operacionState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Mostrar loading
                    }
                    is UiState.Success -> {
                        Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                        viewModel.resetOperacionState()
                    }
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetOperacionState()
                    }
                    is UiState.Idle -> {}
                }
            }
        }
    }

    private fun setupListeners() {
        binding.fabAgregarCategoria.setOnClickListener {
            mostrarDialogoCrearCategoria()
        }
    }

    private fun mostrarDialogoCrearCategoria() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_crear_categoria, null)
        
        val inputNombre = dialogView.findViewById<TextInputEditText>(R.id.inputNombreCategoria)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Nueva Categoría")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = inputNombre.text.toString()
                if (nombre.isNotBlank()) {
                    viewModel.crearCategoria(nombre)
                } else {
                    Toast.makeText(requireContext(), "Ingrese un nombre", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarCategoria(categoria: com.marcos.postresapp.domain.model.Categoria) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_editar_categoria, null)
        
        val inputNombre = dialogView.findViewById<TextInputEditText>(R.id.inputEditarNombreCategoria)
        inputNombre.setText(categoria.nombre)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Categoría")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val nombre = inputNombre.text.toString()
                if (nombre.isNotBlank()) {
                    viewModel.actualizarCategoria(categoria.idCategoria, nombre)
                } else {
                    Toast.makeText(requireContext(), "Ingrese un nombre", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

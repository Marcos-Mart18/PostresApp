package com.marcos.postresapp.presentation.ui.fragment.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder   // 👈 usar Material
import com.marcos.postresapp.R
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.NetworkClient
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.data.repository.AuthRepositoryImpl
import com.marcos.postresapp.domain.dto.ProductoDTO
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.model.Producto
import com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapter
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapterAdmin
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CatalogoAdminFragment : Fragment() {

    private lateinit var rvCategorias: RecyclerView
    private lateinit var productoAdapter: ProductoAdapterAdmin
    private lateinit var categoriaAdapter: CategoriaAdapter

    private lateinit var productApiService: ProductoApiService
    private lateinit var categoriaApiService: CategoriaApiService

    private lateinit var cardAgregar: View
    private lateinit var panelForm: View
    private lateinit var btnCrear: Button

    private lateinit var btnUploadImage: Button
    private lateinit var btnCancel: Button
    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var imgProducto: ImageView
    private lateinit var rvProductos: RecyclerView
    private lateinit var spCategoria: Spinner
    private lateinit var loadingOverlay: View
    private lateinit var lottieLoader: com.airbnb.lottie.LottieAnimationView
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_catalogo_admin, container, false)

        // 🔑 Prefs + Auth + Retrofit (interceptor)
        val prefsManager = PrefsManager(requireContext())
        val authApiService = NetworkClient.createBasic().create(AuthApiService::class.java)
        val authRepository = AuthRepositoryImpl(authApiService, prefsManager)
        val retrofitProtected = NetworkClient.create(prefsManager, authRepository)
        productApiService = retrofitProtected.create(ProductoApiService::class.java)
        categoriaApiService = retrofitProtected.create(CategoriaApiService::class.java)

        // Views
        loadingOverlay = rootView.findViewById(R.id.loadingOverlay)
        lottieLoader = rootView.findViewById(R.id.lottieLoader)
        cardAgregar = rootView.findViewById(R.id.cardAgregar)
        panelForm = rootView.findViewById(R.id.panelForm)
        rvProductos = rootView.findViewById(R.id.adProductos)
        btnCrear = rootView.findViewById(R.id.btnCrear)
        btnUploadImage = rootView.findViewById(R.id.btnSelectImage)
        btnCancel = rootView.findViewById(R.id.btnCancel)
        etNombre = rootView.findViewById(R.id.etNombre)
        etPrecio = rootView.findViewById(R.id.etPrecio)
        etDescripcion = rootView.findViewById(R.id.etDescripcion)
        imgProducto = rootView.findViewById(R.id.imgProducto)
        imgProducto.setImageResource(R.drawable.ic_image_placeholder)
        spCategoria = rootView.findViewById(R.id.spCategoria)

        // Mostrar formulario
        cardAgregar.setOnClickListener {
            rvProductos.visibility = View.GONE
            panelForm.visibility = View.VISIBLE
        }
        // Cancelar formulario
        btnCancel.setOnClickListener { resetForm() }

        // Recycler de productos (adapter con callbacks)
        rvProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        productoAdapter = ProductoAdapterAdmin(
            productos = mutableListOf(),
            onLongPress = { producto, pos -> mostrarDialogoEliminar(producto, pos) },
            onEditClick = { _, _ -> /* abre edición si lo necesitas */ }
        )
        rvProductos.adapter = productoAdapter

        // Recycler de categorías
        rvCategorias = rootView.findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoriaAdapter = CategoriaAdapter(emptyList()) { categoria -> filtrarPorCategoria(categoria) }
        rvCategorias.adapter = categoriaAdapter

        // Chip "Todos"
        val chipTodos: LinearLayout = rootView.findViewById(R.id.chipTodos)
        chipTodos.setOnClickListener { mostrarTodosLosProductos() }

        // Crear producto
        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val precio = etPrecio.text.toString().toDoubleOrNull()
            val descripcion = etDescripcion.text.toString().trim()

            if (nombre.isNotEmpty() && (precio != null && precio > 0) && descripcion.isNotEmpty() && imageUri != null) {
                val categoriaSeleccionada = spCategoria.selectedItem as Categoria
                crearProducto(nombre, precio, descripcion, categoriaSeleccionada.idCategoria)
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        btnUploadImage.setOnClickListener { openImagePicker() }

        cargarProductos()
        cargarCategorias()

        return rootView
    }

    /** Cargar productos */
    private fun cargarProductos() {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                productoAdapter.actualizarLista(productos)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando productos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Cargar categorías */
    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                val categorias = categoriaApiService.getCategorias()
                categoriaAdapter.actualizarLista(categorias)
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spCategoria.adapter = spinnerAdapter
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando categorías: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Filtro por categoría */
    private fun filtrarPorCategoria(categoria: Categoria) {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                val filtrados = productos.filter { it.categoria?.idCategoria == categoria.idCategoria }
                productoAdapter.actualizarLista(filtrados)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al filtrar productos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Mostrar todos los productos */
    private fun mostrarTodosLosProductos() {
        lifecycleScope.launch {
            try {
                val productos = productApiService.getProductos()
                productoAdapter.actualizarLista(productos)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando productos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Crear producto */
    private fun crearProducto(nombre: String, precio: Double, descripcion: String, idCategoria: Int) {
        val producto = ProductoDTO(nombre, precio, descripcion, idCategoria)

        if (imageUri == null) {
            Toast.makeText(requireContext(), "No se ha seleccionado ninguna imagen.", Toast.LENGTH_SHORT).show()
            return
        }
        val file = getFileFromUri(imageUri)
        if (file == null || !file.exists()) {
            Toast.makeText(requireContext(), "El archivo de imagen no existe o no se pudo acceder.", Toast.LENGTH_SHORT).show()
            return
        }

        val productoJson = Gson().toJson(producto)
        val productoRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), productoJson)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

        lifecycleScope.launch {
            setLoading(true)
            try {
                productApiService.createProductoWithImage(productoRequestBody, filePart)
                Toast.makeText(requireContext(), "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                cargarProductos()
                resetForm()
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Error HTTP: ${e.message()} ${e.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("CatalogoFragment", "Error de red: ${e.localizedMessage}", e)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al crear producto: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("CatalogoFragment", "Error al crear producto: ${e.localizedMessage}", e)
            } finally {
                setLoading(false)
            }
        }
    }

    /** Diálogo de confirmación de borrado (Material) */
    private fun mostrarDialogoEliminar(producto: Producto, position: Int) {
        val nombre = producto.nombre ?: "este producto"

        // Por si el overlay está encima, lo ocultamos antes de mostrar el diálogo
        if (loadingOverlay.visibility == View.VISIBLE) setLoading(false)

        MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle("Eliminar producto")
            .setMessage("¿Deseas eliminar \"$nombre\"?")
            .setPositiveButton("Eliminar") { _, _ -> val id = producto.idProducto ?: return@setPositiveButton
                eliminarProducto(id, position) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Llama al DELETE y quita el ítem del adapter */
    private fun eliminarProducto(idProducto: Long, position: Int) {
        lifecycleScope.launch {
            setLoading(true)
            try {
                val resp = productApiService.deleteProducto(idProducto)
                if (resp.isSuccessful) {
                    productoAdapter.removeAt(position)
                    Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo eliminar (${resp.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    /** Picker de imagen */
    private fun openImagePicker() {
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(pickImageIntent, IMAGE_PICK_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imgProducto.setImageURI(imageUri)
        }
    }

    private fun getFileFromUri(uri: Uri?): File? {
        if (uri == null) return null
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().cacheDir, fileName)
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun resetForm() {
        etNombre.setText("")
        etPrecio.setText("")
        etDescripcion.setText("")
        spCategoria.setSelection(0)
        imageUri = null
        imgProducto.setImageResource(R.drawable.ic_image_placeholder)
        rvProductos.visibility = View.VISIBLE
        panelForm.visibility = View.GONE
    }

    private fun setLoading(loading: Boolean) {
        loadingOverlay.visibility = if (loading) View.VISIBLE else View.GONE
        if (loading) lottieLoader.playAnimation() else lottieLoader.cancelAnimation()
        btnCrear.isEnabled = !loading
        btnCancel.isEnabled = !loading
        cardAgregar.isEnabled = !loading
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 1
    }
}

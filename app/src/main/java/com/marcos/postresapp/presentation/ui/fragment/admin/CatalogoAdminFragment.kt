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
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.dto.auth.RefreshTokenRequestDto
import com.marcos.postresapp.presentation.ui.utils.CustomToast
import com.marcos.postresapp.data.remote.dto.producto.ProductoRequestDto
import com.marcos.postresapp.domain.model.Categoria
import com.marcos.postresapp.domain.model.Producto
import com.marcos.postresapp.presentation.ui.adapter.ProductoAdapterAdmin
import com.google.gson.Gson
import com.marcos.postresapp.di.ServiceLocator
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CatalogoAdminFragment : Fragment() {

    private fun createAuthenticatedApiService(): ProductoApiService {
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                // Obtener token DINÁMICAMENTE en cada petición
                val prefs = ServiceLocator.getPrefsManager(requireContext())
                val token = prefs.getAccessToken()
                val refreshToken = prefs.getRefreshToken()
                val roles = prefs.getRoles()
                
                android.util.Log.d("CatalogoAdmin", "=== NUEVA PETICIÓN ===")
                android.util.Log.d("CatalogoAdmin", "Roles del usuario: $roles")
                android.util.Log.d("CatalogoAdmin", "Access token disponible: ${token != null}")
                android.util.Log.d("CatalogoAdmin", "Refresh token disponible: ${refreshToken != null}")
                if (token != null) {
                    android.util.Log.d("CatalogoAdmin", "Token length: ${token.length}")
                    android.util.Log.d("CatalogoAdmin", "Token (primeros 50): ${token.take(50)}...")
                }
                
                val request = if (token != null) {
                    android.util.Log.d("CatalogoAdmin", "✅ Agregando token a petición")
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    android.util.Log.e("CatalogoAdmin", "❌ Token no encontrado!")
                    chain.request()
                }
                
                val response = chain.proceed(request)
                android.util.Log.d("CatalogoAdmin", "Respuesta: ${response.code}")
                
                // Si recibimos 401, mostrar mensaje claro
                if (response.code == 401) {
                    android.util.Log.e("CatalogoAdmin", "❌ Error 401 - Token inválido")
                    activity?.runOnUiThread {
                        CustomToast.error(requireContext(), "Token inválido. Renovando...")
                    }
                }
                
                response
            }
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        
        return retrofit2.Retrofit.Builder()
            .baseUrl("http://10.40.26.157:9090/")
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(ProductoApiService::class.java)
    }

    private fun createCategoriaApiService(): CategoriaApiService {
        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply {
                level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                // Obtener token DINÁMICAMENTE
                val prefs = ServiceLocator.getPrefsManager(requireContext())
                val token = prefs.getAccessToken()
                
                val request = if (token != null) {
                    android.util.Log.d("CatalogoAdmin", "✅ Token categorías: ${token.take(50)}...")
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    android.util.Log.e("CatalogoAdmin", "❌ Token no encontrado para categorías!")
                    chain.request()
                }
                chain.proceed(request)
            }
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        
        return retrofit2.Retrofit.Builder()
            .baseUrl("http://10.40.26.157:9090/")
            .client(client)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(CategoriaApiService::class.java)
    }

    private lateinit var rvCategorias: RecyclerView
    private lateinit var productoAdapter: ProductoAdapterAdmin
    private lateinit var categoriaAdapter: com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapterCatalogo

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
            onEditClick = { producto, _ -> mostrarDialogoEditarProducto(producto) }
        )
        rvProductos.adapter = productoAdapter

        // Recycler de categorías
        rvCategorias = rootView.findViewById(R.id.rvCategorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoriaAdapter = com.marcos.postresapp.presentation.ui.adapter.CategoriaAdapterCatalogo(
            emptyList()
        ) { categoria -> 
            filtrarPorCategoria(categoria) 
        }
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
                Log.d("CatalogoAdmin", "🔄 Cargando productos...")
                val productos = createAuthenticatedApiService().getProductos()
                productoAdapter.actualizarLista(productos)
                Log.d("CatalogoAdmin", "✅ Productos cargados: ${productos.size}")
                
                if (productos.isEmpty()) {
                    CustomToast.info(requireContext(), "No hay productos disponibles")
                }
            } catch (e: HttpException) {
                Log.e("CatalogoAdmin", "❌ Error HTTP ${e.code()}: ${e.message()}")
                
                val errorMsg = when (e.code()) {
                    401 -> {
                        handleSessionExpired()
                        "Sesión expirada"
                    }
                    403 -> "No tienes permisos para ver productos"
                    500 -> "Error del servidor"
                    else -> "Error cargando productos (${e.code()})"
                }
                CustomToast.error(requireContext(), errorMsg)
            } catch (e: IOException) {
                CustomToast.error(requireContext(), "Sin conexión a internet")
                Log.e("CatalogoAdmin", "Error de red: ${e.message}", e)
            } catch (e: Exception) {
                CustomToast.error(requireContext(), "Error inesperado cargando productos")
                Log.e("CatalogoAdmin", "Error: ${e.message}", e)
            }
        }
    }

    /** Cargar categorías */
    private fun cargarCategorias() {
        Log.d("CatalogoAdmin", "🔄 Iniciando carga de categorías...")
        lifecycleScope.launch {
            try {
                val categorias = createCategoriaApiService().getCategorias()
                Log.d("CatalogoAdmin", "✅ Categorías cargadas: ${categorias.size}")
                
                categoriaAdapter.actualizarLista(categorias)
                val spinnerAdapter = com.marcos.postresapp.presentation.ui.adapter.CategoriaSpinnerAdapter(
                    requireContext(),
                    categorias
                )
                spCategoria.adapter = spinnerAdapter
                
                if (categorias.isEmpty()) {
                    CustomToast.warning(requireContext(), "No hay categorías disponibles. Crea una primero")
                }
            } catch (e: HttpException) {
                Log.e("CatalogoAdmin", "❌ Error HTTP ${e.code()}: ${e.message()}")
                
                val errorMsg = when (e.code()) {
                    401 -> {
                        handleSessionExpired()
                        "Sesión expirada al cargar categorías"
                    }
                    403 -> "No tienes permisos para ver categorías"
                    500 -> "Error del servidor"
                    else -> "Error cargando categorías (${e.code()})"
                }
                CustomToast.error(requireContext(), errorMsg)
            } catch (e: IOException) {
                CustomToast.error(requireContext(), "Sin conexión para cargar categorías")
                Log.e("CatalogoAdmin", "Error de red: ${e.message}", e)
            } catch (e: Exception) {
                CustomToast.error(requireContext(), "Error inesperado cargando categorías")
                Log.e("CatalogoAdmin", "Error: ${e.message}", e)
            }
        }
    }

    /** Filtro por categoría */
    private fun filtrarPorCategoria(categoria: Categoria) {
        lifecycleScope.launch {
            try {
                val productos = createAuthenticatedApiService().getProductos()
                val filtrados = productos.filter { it.categoria?.idCategoria == categoria.idCategoria }
                productoAdapter.actualizarLista(filtrados)
            } catch (e: Exception) {
                CustomToast.error(requireContext(), "Error al filtrar productos")
            }
        }
    }

    /** Mostrar todos los productos */
    private fun mostrarTodosLosProductos() {
        lifecycleScope.launch {
            try {
                val productos = createAuthenticatedApiService().getProductos()
                productoAdapter.actualizarLista(productos)
            } catch (e: Exception) {
                CustomToast.error(requireContext(), "Error cargando productos")
            }
        }
    }

    /** Crear producto */
    private fun crearProducto(nombre: String, precio: Double, descripcion: String, idCategoria: Int) {
        val producto = ProductoRequestDto(nombre, precio, descripcion, idCategoria)

        if (imageUri == null) {
            CustomToast.warning(requireContext(), "No se ha seleccionado ninguna imagen")
            return
        }
        val file = getFileFromUri(imageUri)
        if (file == null || !file.exists()) {
            CustomToast.error(requireContext(), "El archivo de imagen no existe o no se pudo acceder")
            return
        }

        lifecycleScope.launch {
            setLoading(true)
            try {
                // Crear RequestBodies reutilizables para evitar problemas con reintentos
                val productoJson = Gson().toJson(producto)
                val productoRequestBody = com.marcos.postresapp.data.remote.utils.ReusableRequestBody.create(
                    "application/json".toMediaTypeOrNull(), 
                    productoJson
                )
                
                // Leer el archivo una sola vez y crear RequestBody reutilizable
                val fileBytes = file.readBytes()
                val requestBody = com.marcos.postresapp.data.remote.utils.ReusableRequestBody.create(
                    "image/*".toMediaTypeOrNull(), 
                    fileBytes
                )
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

                Log.d("CatalogoFragment", "🚀 Creando producto: $nombre")
                createAuthenticatedApiService().createProductoWithImage(productoRequestBody, filePart)
                
                Log.d("CatalogoFragment", "✅ Producto creado exitosamente")
                CustomToast.success(requireContext(), "¡Producto creado con éxito!")
                cargarProductos()
                resetForm()
                
            } catch (e: HttpException) {
                Log.e("CatalogoFragment", "❌ Error HTTP ${e.code()}: ${e.message()}")
                
                val errorMsg = when (e.code()) {
                    401 -> {
                        // El TokenAuthenticator ya manejó el refresh automáticamente
                        // Si llegamos aquí, significa que el refresh falló
                        Log.w("CatalogoFragment", "🔴 Autenticación falló después del refresh automático")
                        handleSessionExpired()
                        "Sesión expirada. Por favor, inicia sesión nuevamente"
                    }
                    403 -> "No tienes permisos para crear productos"
                    409 -> "Ya existe un producto con ese nombre"
                    413 -> "La imagen es demasiado grande"
                    422 -> "Datos del producto inválidos"
                    500 -> "Error del servidor. Inténtalo más tarde"
                    else -> "Error del servidor (${e.code()})"
                }
                CustomToast.error(requireContext(), errorMsg)
                Log.e("CatalogoFragment", "Error HTTP ${e.code()}: ${e.message()}", e)
            } catch (e: IOException) {
                CustomToast.error(requireContext(), "Sin conexión a internet. Verifica tu red")
                Log.e("CatalogoFragment", "Error de red: ${e.localizedMessage}", e)
            } catch (e: Exception) {
                CustomToast.error(requireContext(), "Error inesperado al crear producto")
                Log.e("CatalogoFragment", "Error al crear producto: ${e.localizedMessage}", e)
            } finally {
                setLoading(false)
            }
        }
    }

    /** Maneja la expiración de sesión */
    private fun handleSessionExpired() {
        Log.w("CatalogoFragment", "🔴 Sesión expirada - Limpiando datos y redirigiendo")
        
        lifecycleScope.launch {
            try {
                // Limpiar datos de usuario
                val prefs = ServiceLocator.getPrefsManager(requireContext())
                prefs.clearUserData()
                
                // Redirigir al login
                val intent = Intent(requireContext(), com.marcos.postresapp.presentation.ui.activity.auth.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
                
            } catch (e: Exception) {
                Log.e("CatalogoFragment", "❌ Error en logout automático: ${e.message}")
                CustomToast.error(requireContext(), "Error al cerrar sesión. Hazlo manualmente")
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
                Log.d("CatalogoAdmin", "🗑️ Eliminando producto ID: $idProducto")
                val resp = createAuthenticatedApiService().deleteProducto(idProducto)
                
                if (resp.isSuccessful) {
                    productoAdapter.removeAt(position)
                    CustomToast.success(requireContext(), "Producto eliminado exitosamente")
                    Log.d("CatalogoAdmin", "✅ Producto eliminado")
                } else {
                    val errorMsg = when (resp.code()) {
                        401 -> {
                            handleSessionExpired()
                            "Sesión expirada"
                        }
                        403 -> "No tienes permisos para eliminar productos"
                        404 -> "Producto no encontrado"
                        else -> "No se pudo eliminar (${resp.code()})"
                    }
                    CustomToast.error(requireContext(), errorMsg)
                }
            } catch (e: HttpException) {
                Log.e("CatalogoAdmin", "❌ Error HTTP ${e.code()}: ${e.message()}")
                
                val errorMsg = when (e.code()) {
                    401 -> {
                        handleSessionExpired()
                        "Sesión expirada"
                    }
                    403 -> "No tienes permisos para eliminar productos"
                    404 -> "Producto no encontrado"
                    else -> "Error al eliminar (${e.code()})"
                }
                CustomToast.error(requireContext(), errorMsg)
            } catch (e: IOException) {
                CustomToast.error(requireContext(), "Sin conexión a internet")
                Log.e("CatalogoAdmin", "Error de red: ${e.message}", e)
            } catch (e: Exception) {
                CustomToast.error(requireContext(), "Error inesperado al eliminar")
                Log.e("CatalogoAdmin", "Error: ${e.message}", e)
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
        when (requestCode) {
            IMAGE_PICK_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUri = data?.data
                    imgProducto.setImageURI(imageUri)
                }
            }
            IMAGE_EDIT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    editImageUri = data?.data
                    // La imagen se actualizará en el dialog cuando se reabra
                }
            }
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

    private var editingProducto: Producto? = null
    private var editImageUri: Uri? = null

    private fun mostrarDialogoEditarProducto(producto: Producto) {
        editingProducto = producto
        editImageUri = null

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_editar_producto, null)

        val imgProducto = dialogView.findViewById<ImageView>(R.id.imgEditarProducto)
        val btnEditarImagen = dialogView.findViewById<Button>(R.id.btnEditarImagen)
        val inputNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputEditarNombre)
        val inputPrecio = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputEditarPrecio)
        val inputDescripcion = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputEditarDescripcion)
        val spCategoria = dialogView.findViewById<Spinner>(R.id.spEditarCategoria)

        // Cargar datos actuales
        inputNombre.setText(producto.nombre)
        inputPrecio.setText(producto.precio.toString())
        inputDescripcion.setText(producto.descripcion)

        // Cargar imagen actual
        com.bumptech.glide.Glide.with(this)
            .load(producto.fotoUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(imgProducto)

        // Cargar categorías en spinner
        lifecycleScope.launch {
            try {
                val categorias = createCategoriaApiService().getCategorias()
                val spinnerAdapter = com.marcos.postresapp.presentation.ui.adapter.CategoriaSpinnerAdapter(
                    requireContext(),
                    categorias
                )
                spCategoria.adapter = spinnerAdapter

                // Seleccionar categoría actual
                val currentCategoriaIndex = categorias.indexOfFirst { it.idCategoria == producto.categoria?.idCategoria }
                if (currentCategoriaIndex >= 0) {
                    spCategoria.setSelection(currentCategoriaIndex)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando categorías", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para cambiar imagen
        btnEditarImagen.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(pickImageIntent, IMAGE_EDIT_REQUEST)
        }

        // Mostrar dialog
        MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val nombre = inputNombre.text.toString().trim()
                val precio = inputPrecio.text.toString().toDoubleOrNull()
                val descripcion = inputDescripcion.text.toString().trim()
                val categoriaSeleccionada = spCategoria.selectedItem as? Categoria

                if (nombre.isNotEmpty() && precio != null && precio > 0 && descripcion.isNotEmpty() && categoriaSeleccionada != null) {
                    actualizarProducto(
                        producto.idProducto ?: return@setPositiveButton,
                        nombre,
                        precio,
                        descripcion,
                        categoriaSeleccionada.idCategoria,
                        editImageUri
                    )
                } else {
                    Toast.makeText(requireContext(), "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarProducto(
        idProducto: Long,
        nombre: String,
        precio: Double,
        descripcion: String,
        idCategoria: Int,
        newImageUri: Uri?
    ) {
        lifecycleScope.launch {
            setLoading(true)
            try {
                val productoRequest = ProductoRequestDto(nombre, precio, descripcion, idCategoria)
                val productoJson = Gson().toJson(productoRequest)
                val productoRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), productoJson)

                if (newImageUri != null) {
                    // Actualizar con nueva imagen
                    val file = getFileFromUri(newImageUri)
                    if (file != null && file.exists()) {
                        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                        createAuthenticatedApiService().updateProductoWithImage(idProducto, productoRequestBody, filePart)
                    } else {
                        Toast.makeText(requireContext(), "Error con la imagen", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                } else {
                    // Actualizar sin cambiar imagen
                    createAuthenticatedApiService().updateProducto(idProducto, productoRequest)
                }

                Toast.makeText(requireContext(), "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                cargarProductos()
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Error HTTP: ${e.message()} ${e.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al actualizar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 1
        private const val IMAGE_EDIT_REQUEST = 2
    }
}

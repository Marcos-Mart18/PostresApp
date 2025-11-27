package com.marcos.postresapp.presentation.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.marcos.postresapp.R
import com.marcos.postresapp.presentation.state.UiState
import com.marcos.postresapp.presentation.ui.activity.admin.HomeAdminActivity
import com.marcos.postresapp.presentation.ui.activity.repartidor.HomeRepartidorActivity
import com.marcos.postresapp.presentation.ui.activity.user.HomeUserActivity
import com.marcos.postresapp.presentation.viewmodel.auth.LoginViewModel
import com.marcos.postresapp.presentation.ui.utils.CustomToast
import com.marcos.postresapp.di.ServiceLocator
import com.marcos.postresapp.domain.usecase.auth.LoginUseCase
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by lazy {
        val repository = ServiceLocator.getAuthRepository(this)
        val loginUseCase = LoginUseCase(repository)
        val factory = LoginViewModelFactory(loginUseCase, repository)
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private lateinit var txtUsuario: TextInputEditText
    private lateinit var txtContrasena: TextInputEditText
    private lateinit var btnGoMain: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de las vistas
        txtUsuario = findViewById(R.id.txtUsuario)
        txtContrasena = findViewById(R.id.txtContrasena)
        btnGoMain = findViewById(R.id.btnGoMain)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            hideLoading()
                        }
                        
                        is UiState.Loading -> {
                            showLoading()
                        }
                        
                        is UiState.Success -> {
                            hideLoading()
                            val user = state.data
                            val roleText = when {
                                "ADMIN" in user.roles -> "Administrador"
                                "REPARTIDOR" in user.roles -> "Repartidor"
                                else -> "Usuario"
                            }
                            CustomToast.success(
                                this@LoginActivity,
                                "¡Bienvenido ${user.username}! ($roleText)"
                            )
                            
                            // Navegar según el rol
                            navigateByRole()
                        }
                        
                        is UiState.Error -> {
                            hideLoading()
                            val errorMsg = when {
                                state.message.contains("401") || state.message.contains("Unauthorized") -> 
                                    "Usuario o contraseña incorrectos"
                                state.message.contains("network") || state.message.contains("timeout") -> 
                                    "Sin conexión al servidor. Verifica tu red"
                                state.message.contains("500") -> 
                                    "Error del servidor. Inténtalo más tarde"
                                else -> "Error de inicio de sesión: ${state.message}"
                            }
                            CustomToast.error(this@LoginActivity, errorMsg)
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        btnGoMain.setOnClickListener {
            val username = txtUsuario.text.toString().trim()
            val password = txtContrasena.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            
            CustomToast.info(this, "Iniciando sesión...")
            viewModel.login(username, password)
        }
    }

    private fun navigateByRole() {
        val roles = viewModel.getUserRoles()
        
        val intent = when {
            "ADMIN" in roles -> Intent(this, HomeAdminActivity::class.java)
            "REPARTIDOR" in roles -> Intent(this, HomeRepartidorActivity::class.java)
            else -> Intent(this, HomeUserActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        btnGoMain.isEnabled = false
        txtUsuario.isEnabled = false
        txtContrasena.isEnabled = false
    }

    private fun hideLoading() {
        btnGoMain.isEnabled = true
        txtUsuario.isEnabled = true
        txtContrasena.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetState()
    }
}

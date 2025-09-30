package com.marcos.postresapp.presentation.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marcos.postresapp.R
import com.marcos.postresapp.presentation.ui.activity.user.HomeUserActivity
import com.marcos.postresapp.presentation.ui.activity.admin.HomeAdminActivity
import com.marcos.postresapp.presentation.ui.activity.repartidor.HomeRepartidorActivity
import com.marcos.postresapp.presentation.viewmodel.LoginViewModel
import com.marcos.postresapp.presentation.viewmodel.LoginViewModelFactory
import com.marcos.postresapp.data.local.PrefsManager

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(this) }

    private lateinit var txtUsuario: TextInputEditText
    private lateinit var txtContrasena: TextInputEditText
    private lateinit var btnGoMain: Button

    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inicialización de las vistas
        txtUsuario = findViewById(R.id.txtUsuario)
        txtContrasena = findViewById(R.id.txtContrasena)
        btnGoMain = findViewById(R.id.btnGoMain)

        // Inicialización de PrefsManager
        prefsManager = PrefsManager(this)

        btnGoMain.setOnClickListener {
            val username = txtUsuario.text.toString()
            val password = txtContrasena.text.toString()

            // Llamada a la función loginUser
            loginUser(username, password)
        }
    }

    private fun loginUser(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            loginViewModel.login(
                username,
                password,
                onSuccess = { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    // Obtenemos el rol del usuario desde PrefsManager
                    val roles = prefsManager.getRoles()

                    // Redirigir según el rol
                    when {
                        "ADMIN" in roles -> {
                            // Redirigir a la vista de administrador
                            val intent = Intent(this, HomeAdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "REPARTIDOR" in roles -> {
                            // Redirigir a la vista de repartidor
                            val intent = Intent(this, HomeRepartidorActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            // Redirigir a la vista del usuario (Cliente)
                            val intent = Intent(this, HomeUserActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                },
                onError = { error ->
                    // Mostrar mensaje de error si ocurre algo al hacer login
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Si los campos están vacíos, mostrar un mensaje de error
            Toast.makeText(this, "Por favor ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
        }
    }
}

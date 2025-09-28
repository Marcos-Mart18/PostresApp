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
import com.marcos.postresapp.presentation.ui.activity.user.CatalogoActivity
import com.marcos.postresapp.presentation.viewmodel.LoginViewModel
import com.marcos.postresapp.presentation.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    // ViewModel creado para esta Activity
    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(this) }

    // Declaración de las vistas
    private lateinit var txtUsuario: TextInputEditText
    private lateinit var txtContrasena: TextInputEditText
    private lateinit var btnGoMain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inicialización de las vistas
        txtUsuario = findViewById(R.id.txtUsuario)
        txtContrasena = findViewById(R.id.txtContrasena)
        btnGoMain = findViewById(R.id.btnGoMain)

        // Acción del botón "Aceptar"
        btnGoMain.setOnClickListener {
            val username = txtUsuario.text.toString()
            val password = txtContrasena.text.toString()

            // Llamada a la función loginUser
            loginUser(username, password)
        }
    }

    // loginUser en LoginActivity
    private fun loginUser(username: String, password: String) {
        // Verificar que los campos no estén vacíos
        if (username.isNotEmpty() && password.isNotEmpty()) {
            loginViewModel.login(
                username,
                password,
                onSuccess = { message ->  // 'message' es un saludo con el nombre del usuario
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CatalogoActivity::class.java)
                    startActivity(intent)
                    finish()  // Para que no se pueda volver a la pantalla de login
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

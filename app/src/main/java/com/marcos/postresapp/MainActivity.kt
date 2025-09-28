package com.marcos.postresapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.marcos.postresapp.presentation.ui.activity.auth.LoginActivity
import com.marcos.postresapp.presentation.ui.activity.auth.RegisterActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnGotoRegister = findViewById<Button>(R.id.btnGotoRegister)
        val btnGotoLogin = findViewById<Button>(R.id.btnGotoLogin)

        btnGotoRegister.setOnClickListener {
            btnRegister()
        }

        btnGotoLogin.setOnClickListener {
            btnLogin()
        }
    }

    private fun btnRegister(){
        val i = Intent(this, RegisterActivity::class.java)
        startActivity(i)
    }

    private fun btnLogin(){
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
    }
}
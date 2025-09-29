package com.marcos.postresapp.presentation.ui.activity.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.marcos.postresapp.R

class HomeAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_admin)
        val cardAgregar     = findViewById<View>(R.id.cardAgregar)
        val panelForm       = findViewById<View>(R.id.panelForm)
        val rvProductos     = findViewById<View>(R.id.rvProductos)
        val btnCrear        = findViewById<View>(R.id.btnCrear)

        cardAgregar.setOnClickListener {
            rvProductos.visibility = View.GONE
            panelForm.visibility   = View.VISIBLE
        }

        btnCrear.setOnClickListener {
            panelForm.visibility   = View.GONE
            rvProductos.visibility = View.VISIBLE
        }

        }
}
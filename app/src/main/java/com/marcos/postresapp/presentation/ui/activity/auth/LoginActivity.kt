package com.marcos.postresapp.presentation.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.marcos.postresapp.R
import com.marcos.postresapp.presentation.ui.activity.user.CatalogoActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val btnGotoMain = findViewById<Button>(R.id.btnGoMain)


        btnGotoMain.setOnClickListener {
            btnPass()
        }

    }


    private fun btnPass(){
        val i = Intent(this, CatalogoActivity::class.java)
        startActivity(i)
    }
}
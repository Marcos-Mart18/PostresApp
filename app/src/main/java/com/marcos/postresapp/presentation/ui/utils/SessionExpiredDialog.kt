package com.marcos.postresapp.presentation.ui.utils

import android.content.Context
import android.content.Intent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.marcos.postresapp.presentation.ui.activity.auth.LoginActivity

object SessionExpiredDialog {
    
    fun show(context: Context, onRetry: (() -> Unit)? = null) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Sesión Expirada")
            .setMessage("Tu sesión ha expirado. ¿Qué deseas hacer?")
            .setPositiveButton("Ir al Login") { _, _ ->
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .apply {
                if (onRetry != null) {
                    setNeutralButton("Reintentar") { _, _ ->
                        onRetry()
                    }
                }
            }
            .setCancelable(false)
            .show()
    }
}
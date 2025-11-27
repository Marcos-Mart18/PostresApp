package com.marcos.postresapp.presentation.ui.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.marcos.postresapp.R

object CustomToast {
    
    enum class Type {
        SUCCESS, ERROR, WARNING, INFO
    }
    
    fun show(context: Context, message: String, type: Type, duration: Int = Toast.LENGTH_SHORT) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        
        val icon = layout.findViewById<ImageView>(R.id.toast_icon)
        val text = layout.findViewById<TextView>(R.id.toast_text)
        
        text.text = message
        
        when (type) {
            Type.SUCCESS -> {
                icon.setImageResource(R.drawable.ic_success)
                layout.setBackgroundResource(R.drawable.toast_success_bg)
            }
            Type.ERROR -> {
                icon.setImageResource(R.drawable.ic_error)
                layout.setBackgroundResource(R.drawable.toast_error_bg)
            }
            Type.WARNING -> {
                icon.setImageResource(R.drawable.ic_warning)
                layout.setBackgroundResource(R.drawable.toast_warning_bg)
            }
            Type.INFO -> {
                icon.setImageResource(R.drawable.ic_info)
                layout.setBackgroundResource(R.drawable.toast_info_bg)
            }
        }
        
        val toast = Toast(context)
        toast.duration = duration
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.show()
    }
    
    fun success(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        show(context, message, Type.SUCCESS, duration)
    }
    
    fun error(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
        show(context, message, Type.ERROR, duration)
    }
    
    fun warning(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        show(context, message, Type.WARNING, duration)
    }
    
    fun info(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        show(context, message, Type.INFO, duration)
    }
}
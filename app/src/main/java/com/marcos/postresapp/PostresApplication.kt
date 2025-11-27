package com.marcos.postresapp

import android.app.Application
import com.marcos.postresapp.di.ServiceLocator

class PostresApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}

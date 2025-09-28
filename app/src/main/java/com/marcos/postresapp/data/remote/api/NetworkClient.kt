package com.marcos.postresapp.data.remote.api

import android.content.Context
import com.marcos.postresapp.data.local.PrefsManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private const val BASE_URL = "http://10.40.33.59:9090/"

    // Interceptor para agregar el token en el header de cada solicitud
    private fun provideOkHttpClient(prefsManager: PrefsManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Agregar el token de autorización si está disponible
                prefsManager.getAccessToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Crear instancia de Retrofit con OkHttpClient
    fun create(prefsManager: PrefsManager): Retrofit {
        val client = provideOkHttpClient(prefsManager)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Usamos el cliente con interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

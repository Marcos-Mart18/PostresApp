package com.marcos.postresapp.data.remote.api

import android.util.Log
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.models.RefreshTokenRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private const val BASE_URL = "http://10.40.49.47:9090/"

    // Interceptor para agregar el token en el header de cada solicitud
    private fun provideOkHttpClient(prefsManager: PrefsManager, authApiService: AuthApiService): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val token = prefsManager.getAccessToken()
                var request = chain.request()

                if (token != null) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }

                var response = chain.proceed(request)

                // Si obtenemos un error 401 (token expirado), intentar refrescar el token
                if (response.code == 401) {
                    val refreshToken = prefsManager.getRefreshToken()

                    // Si tenemos un refresh token, intentar obtener un nuevo access token
                    if (refreshToken != null) {
                        // Aquí solo retornamos el estado de error 401, el refresco se hará fuera del interceptor
                        throw TokenExpiredException("Token expired, please refresh the token")
                    }
                }

                response
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
    fun create(prefsManager: PrefsManager, authApiService: AuthApiService): Retrofit {
        val client = provideOkHttpClient(prefsManager, authApiService)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Usamos el cliente con interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

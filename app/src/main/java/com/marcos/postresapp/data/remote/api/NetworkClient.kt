package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private const val BASE_URL = "http://10.40.55.80:9090/"

    // Retrofit sin interceptor (para login, refresh y logout)
    fun createBasic(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit con interceptor (para servicios protegidos con token)
    fun create(
        prefsManager: PrefsManager,
        authRepository: AuthRepositoryImpl
    ): Retrofit {
        val client = provideOkHttpClient(prefsManager, authRepository)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Cliente con interceptor de token
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cliente con logging + manejo automático de refresh token
    private fun provideOkHttpClient(
        prefsManager: PrefsManager,
        authRepository: AuthRepositoryImpl
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val token = prefsManager.getAccessToken()
                var request = chain.request()

                // 👉 Añadir el accessToken al header si existe
                if (token != null) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }

                var response = chain.proceed(request)

                // 👉 Si obtenemos un error 401, intentamos refrescar el token
                if (response.code == 401) {
                    val refreshToken = prefsManager.getRefreshToken()

                    if (refreshToken != null) {
                        try {
                            // 🔄 Llamamos a AuthRepositoryImpl.refreshAccessToken()
                            val newAccessToken = runBlocking {
                                authRepository.refreshAccessToken(refreshToken)
                            }

                            // Guardamos el nuevo token en PrefsManager
                            prefsManager.saveAccessToken(newAccessToken)

                            // Reintentamos la petición original con el nuevo token
                            request = request.newBuilder()
                                .addHeader("Authorization", "Bearer $newAccessToken")
                                .build()

                            response.close()
                            response = chain.proceed(request)
                        } catch (e: Exception) {
                            throw TokenExpiredException("No se pudo refrescar el token")
                        }
                    }
                }

                response
            }
            .build()
    }
}

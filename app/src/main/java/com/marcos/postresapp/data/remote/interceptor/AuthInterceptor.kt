package com.marcos.postresapp.data.remote.interceptor

import android.util.Log
import com.marcos.postresapp.data.local.PrefsManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val prefsManager: PrefsManager
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        
        // Endpoints que no requieren autenticación
        private val PUBLIC_ENDPOINTS = setOf(
            "/auth/login",
            "/auth/register", 
            "/auth/refresh"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        
        // Verificar si es un endpoint público
        val isPublicEndpoint = PUBLIC_ENDPOINTS.any { endpoint -> 
            url.contains(endpoint) 
        }
        
        val request = when {
            // Si ya tiene header Authorization, no lo tocamos (viene del Authenticator)
            originalRequest.header(AUTHORIZATION_HEADER) != null -> {
                Log.v(TAG, "⏭️ Header Authorization ya presente, manteniendo: ${getEndpointName(url)}")
                originalRequest
            }
            
            // Si es endpoint público, no añadimos token
            isPublicEndpoint -> {
                Log.v(TAG, "🌐 Endpoint público, sin token: ${getEndpointName(url)}")
                originalRequest
            }
            
            // Para endpoints privados, añadir token si existe
            else -> {
                val token = prefsManager.getAccessToken()
                
                if (token != null && token.isNotBlank()) {
                    Log.v(TAG, "🔑 Añadiendo token a: ${getEndpointName(url)}")
                    Log.v(TAG, "Token: ${token.take(20)}...${token.takeLast(10)}")
                    
                    originalRequest.newBuilder()
                        .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
                        .build()
                } else {
                    Log.w(TAG, "⚠️ Sin token para endpoint privado: ${getEndpointName(url)}")
                    originalRequest
                }
            }
        }
        
        // Procesar la petición
        val startTime = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime
        
        // Log de respuesta
        logResponse(response, url, duration)
        
        return response
    }
    
    private fun getEndpointName(url: String): String {
        return try {
            val uri = java.net.URI(url)
            uri.path ?: url
        } catch (e: Exception) {
            url
        }
    }
    
    private fun logResponse(response: Response, url: String, duration: Long) {
        val endpoint = getEndpointName(url)
        
        when (response.code) {
            in 200..299 -> {
                Log.v(TAG, "✅ ${response.code} ${endpoint} (${duration}ms)")
            }
            401 -> {
                Log.w(TAG, "❌ 401 Unauthorized: $endpoint - TokenAuthenticator se encargará")
            }
            403 -> {
                Log.w(TAG, "❌ 403 Forbidden: $endpoint - Sin permisos")
            }
            in 400..499 -> {
                Log.w(TAG, "❌ ${response.code} Client Error: $endpoint")
            }
            in 500..599 -> {
                Log.e(TAG, "❌ ${response.code} Server Error: $endpoint")
            }
            else -> {
                Log.d(TAG, "ℹ️ ${response.code} $endpoint (${duration}ms)")
            }
        }
    }
}
package com.marcos.postresapp.data.remote.interceptor

import android.util.Log
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.dto.auth.RefreshTokenRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TokenAuthenticator(
    private val prefsManager: PrefsManager,
    private val baseUrl: String
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
        private const val MAX_RETRY_COUNT = 3
    }

    private val refreshLock = ReentrantLock()
    @Volatile
    private var isRefreshing = false
    @Volatile
    private var retryCount = 0

    override fun authenticate(route: Route?, response: Response): Request? {
        val url = response.request.url.toString()
        Log.d(TAG, "🔄 Autenticación requerida para: ${getEndpointName(url)}")
        
        // No intentar refresh en endpoints de auth para evitar bucles
        if (isAuthEndpoint(url)) {
            Log.w(TAG, "🚫 No se puede hacer refresh en endpoint de auth: ${getEndpointName(url)}")
            return null
        }
        
        // Verificar límite de reintentos usando responseCount de OkHttp
        val responseCount = getResponseCount(response)
        if (responseCount >= MAX_RETRY_COUNT) {
            Log.e(TAG, "🚫 Máximo de reintentos alcanzado ($responseCount >= $MAX_RETRY_COUNT)")
            prefsManager.clearTokens()
            return null
        }
        
        return refreshLock.withLock {
            try {
                // Si ya estamos refrescando, esperar un poco
                if (isRefreshing) {
                    Log.d(TAG, "⏳ Refresh en progreso, esperando...")
                    Thread.sleep(100) // Pequeña espera
                    
                    // Verificar si el token cambió mientras esperábamos
                    val currentToken = prefsManager.getAccessToken()
                    val requestToken = getTokenFromRequest(response.request)
                    
                    if (currentToken != null && currentToken != requestToken) {
                        Log.d(TAG, "✅ Token actualizado mientras esperábamos")
                        return@withLock buildRequestWithToken(response.request, currentToken)
                    }
                    return@withLock null
                }
                
                val currentToken = prefsManager.getAccessToken()
                val requestToken = getTokenFromRequest(response.request)
                
                // Si el token ya cambió (otro hilo lo refrescó), usar el nuevo
                if (currentToken != null && currentToken != requestToken && currentToken.isNotBlank()) {
                    Log.d(TAG, "✅ Token ya actualizado por otro hilo")
                    return@withLock buildRequestWithToken(response.request, currentToken)
                }
                
                // Intentar refresh
                isRefreshing = true
                retryCount++
                
                val newToken = refreshToken()
                if (newToken != null && newToken.isNotBlank()) {
                    Log.d(TAG, "✅ Token renovado exitosamente (intento $retryCount)")
                    retryCount = 0 // Reset counter on success
                    return@withLock buildRequestWithToken(response.request, newToken)
                } else {
                    Log.e(TAG, "❌ No se pudo renovar el token (intento $retryCount)")
                    return@withLock null
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error en authenticate: ${e.message}")
                return@withLock null
            } finally {
                isRefreshing = false
            }
        }
    }
    
    private fun buildRequestWithToken(originalRequest: Request, token: String): Request {
        return originalRequest.newBuilder()
            .removeHeader("Authorization") // Remover header anterior si existe
            .header("Authorization", "Bearer $token")
            .build()
    }
    
    private fun isAuthEndpoint(url: String): Boolean {
        return url.contains("/auth/login") || 
               url.contains("/auth/register") || 
               url.contains("/auth/refresh")
    }
    
    private fun getEndpointName(url: String): String {
        return try {
            val uri = java.net.URI(url)
            uri.path ?: url
        } catch (e: Exception) {
            url
        }
    }
    
    private fun getResponseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    private fun refreshToken(): String? {
        return runBlocking {
            try {
                val refreshToken = prefsManager.getRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    Log.e(TAG, "❌ No hay refresh token disponible")
                    prefsManager.clearTokens()
                    return@runBlocking null
                }

                Log.d(TAG, "🔄 Iniciando refresh del token...")
                Log.v(TAG, "Refresh token: ${refreshToken.take(20)}...${refreshToken.takeLast(10)}")
                
                // Crear cliente básico sin interceptores para evitar bucles
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val authService = retrofit.create(AuthApiService::class.java)
                val request = RefreshTokenRequestDto(refreshToken)
                
                val startTime = System.currentTimeMillis()
                val response = authService.refreshAccessToken(request)
                val duration = System.currentTimeMillis() - startTime
                
                // Validar respuesta
                if (response.accessToken.isBlank()) {
                    Log.e(TAG, "❌ Token recibido está vacío")
                    return@runBlocking null
                }

                // Guardar el nuevo token de forma síncrona
                prefsManager.saveAccessToken(response.accessToken)
                
                Log.d(TAG, "✅ Token renovado y guardado exitosamente (${duration}ms)")
                Log.v(TAG, "Nuevo token: ${response.accessToken.take(20)}...${response.accessToken.takeLast(10)}")
                
                return@runBlocking response.accessToken

            } catch (e: retrofit2.HttpException) {
                Log.e(TAG, "❌ Error HTTP ${e.code()} al renovar token: ${e.message()}")
                
                when (e.code()) {
                    401, 403 -> {
                        Log.w(TAG, "🚪 Refresh token expirado o inválido, limpiando sesión")
                        prefsManager.clearTokens()
                    }
                    429 -> {
                        Log.w(TAG, "⏳ Rate limit alcanzado, reintentando más tarde")
                    }
                    500, 502, 503 -> {
                        Log.w(TAG, "🔧 Error del servidor, el token puede seguir siendo válido")
                    }
                }
                
                return@runBlocking null
                
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "❌ Timeout al renovar token: ${e.message}")
                return@runBlocking null
                
            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "❌ Sin conexión al renovar token: ${e.message}")
                return@runBlocking null
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error inesperado al renovar token: ${e.message}")
                return@runBlocking null
            }
        }
    }

    private fun getTokenFromRequest(request: Request): String? {
        val authHeader = request.header("Authorization")
        return if (authHeader?.startsWith("Bearer ") == true) {
            authHeader.substring(7)
        } else {
            null
        }
    }
}
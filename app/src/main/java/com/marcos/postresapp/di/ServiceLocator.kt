package com.marcos.postresapp.di

import android.content.Context
import com.marcos.postresapp.data.local.PrefsManager
import com.marcos.postresapp.data.remote.api.AuthApiService
import com.marcos.postresapp.data.remote.api.CategoriaApiService
import com.marcos.postresapp.data.remote.api.PedidoApiService
import com.marcos.postresapp.data.remote.api.ProductoApiService
import com.marcos.postresapp.data.remote.api.RepartidorApiService
import com.marcos.postresapp.data.remote.interceptor.AuthInterceptor
import com.marcos.postresapp.data.remote.interceptor.TokenAuthenticator
import com.marcos.postresapp.data.repository.auth.AuthRepositoryImpl
import com.marcos.postresapp.data.repository.categoria.CategoriaRepositoryImpl
import com.marcos.postresapp.data.repository.pedido.PedidoRepositoryImpl
import com.marcos.postresapp.data.repository.producto.ProductoRepositoryImpl
import com.marcos.postresapp.domain.repository.auth.AuthRepository
import com.marcos.postresapp.domain.repository.CategoriaRepository
import com.marcos.postresapp.domain.repository.pedido.PedidoRepository
import com.marcos.postresapp.domain.repository.ProductoRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceLocator {
    private const val BASE_URL = "http://10.40.26.157:9090/"
    
    private var prefsManager: PrefsManager? = null
    private var authRepository: AuthRepository? = null
    private var productoRepository: ProductoRepository? = null
    private var categoriaRepository: CategoriaRepository? = null
    private var pedidoRepository: PedidoRepository? = null
    
    fun init(context: Context) {
        prefsManager = PrefsManager(context.applicationContext)
    }
    
    fun getPrefsManager(context: Context): PrefsManager {
        return prefsManager ?: PrefsManager(context.applicationContext).also { prefsManager = it }
    }
    
    fun getAuthRepository(context: Context): AuthRepository {
        return authRepository ?: AuthRepositoryImpl(
            getAuthApiService(),
            getPrefsManager(context)
        ).also { authRepository = it }
    }
    
    fun getProductoRepository(context: Context): ProductoRepository {
        return productoRepository ?: ProductoRepositoryImpl(
            getProductoApiService(context)
        ).also { productoRepository = it }
    }
    
    fun getCategoriaRepository(context: Context): CategoriaRepository {
        return categoriaRepository ?: CategoriaRepositoryImpl(
            getCategoriaApiService(context)
        ).also { categoriaRepository = it }
    }
    
    fun getPedidoRepository(): PedidoRepository {
        return pedidoRepository ?: PedidoRepositoryImpl(
            getPedidoApiService()
        ).also { pedidoRepository = it }
    }
    
    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    private fun getBasicOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private fun getAuthenticatedOkHttpClient(context: Context): OkHttpClient {
        val prefs = getPrefsManager(context)
        return OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(AuthInterceptor(prefs))
            .authenticator(TokenAuthenticator(prefs, BASE_URL))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private fun getBasicRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getBasicOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private fun getAuthenticatedRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getAuthenticatedOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private fun getAuthApiService(): AuthApiService {
        return getBasicRetrofit().create(AuthApiService::class.java)
    }
    
    private fun getProductoApiService(context: Context): ProductoApiService {
        return getAuthenticatedRetrofit(context).create(ProductoApiService::class.java)
    }
    
    private fun getCategoriaApiService(context: Context): CategoriaApiService {
        return getAuthenticatedRetrofit(context).create(CategoriaApiService::class.java)
    }
    
    private fun getPedidoApiService(): PedidoApiService {
        return getBasicRetrofit().create(PedidoApiService::class.java)
    }
    
    private fun getRepartidorApiService(): RepartidorApiService {
        return getBasicRetrofit().create(RepartidorApiService::class.java)
    }
    
    // Use Cases
    fun provideGetCategoriasUseCase(context: Context): com.marcos.postresapp.domain.usecase.categoria.GetCategoriasUseCase {
        return com.marcos.postresapp.domain.usecase.categoria.GetCategoriasUseCase(getCategoriaRepository(context))
    }
    
    fun provideCrearCategoriaUseCase(context: Context): com.marcos.postresapp.domain.usecase.categoria.CrearCategoriaUseCase {
        return com.marcos.postresapp.domain.usecase.categoria.CrearCategoriaUseCase(getCategoriaRepository(context))
    }
    
    fun provideActualizarCategoriaUseCase(context: Context): com.marcos.postresapp.domain.usecase.categoria.ActualizarCategoriaUseCase {
        return com.marcos.postresapp.domain.usecase.categoria.ActualizarCategoriaUseCase(getCategoriaRepository(context))
    }
}

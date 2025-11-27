package com.marcos.postresapp.data.remote.api

import com.marcos.postresapp.domain.model.Repartidor
import retrofit2.http.GET

interface RepartidorApiService {
    
    @GET("api/v1/repartidores")
    suspend fun getRepartidores(): List<Repartidor>
}

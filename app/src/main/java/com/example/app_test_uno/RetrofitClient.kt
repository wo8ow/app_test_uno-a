package com.example.app_test_uno

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // SOLUCIÓN: Solo la ruta del directorio, terminando en "/"
    private const val BASE_URL = "http://10.0.2.2/Examen1/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
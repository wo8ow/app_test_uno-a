package com.example.app_test_uno

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @GET("api.php")
    suspend fun getTareas(): Response<ApiResponse>

    @POST("api.php")
    suspend fun crearTarea(
        @Body tarea: CrearTareaRequest
    ): Response<ApiResponse>

    @PUT("api.php")
    suspend fun actualizarTarea(
        @Body tarea: Tarea
    ): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "api.php", hasBody = true)
    suspend fun eliminarTarea(
        @Body tarea: Tarea
    ): Response<ApiResponse>
}
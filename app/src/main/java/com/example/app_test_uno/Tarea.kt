package com.example.app_test_uno

import com.google.gson.annotations.SerializedName

data class Tarea(
    val id: Int? = null,
    val titulo: String,
    val descripcion: String?,
    val estado: String? = "pendiente",
    @SerializedName("fecha_creacion") val fechaCreacion: String? = null
)

// Clases de respuesta genericas de nuestra API
data class ApiResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Tarea>?
)
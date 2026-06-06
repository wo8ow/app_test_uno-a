package com.example.app_test_uno

import com.google.gson.annotations.SerializedName

data class Tarea(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("titulo")
    val titulo: String = "",

    @SerializedName("descripcion")
    val descripcion: String? = "",

    @SerializedName("estado")
    val estado: String? = "pendiente",

    @SerializedName("fecha_creacion")
    val fechaCreacion: String? = null
)

data class CrearTareaRequest(
    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String? = "",

    @SerializedName("estado")
    val estado: String = "pendiente"
)

data class ApiResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: List<Tarea>? = emptyList()
)
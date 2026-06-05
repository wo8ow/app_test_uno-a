package com.example.app_test_uno

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asume que tienes tu UI aquí

        // Ejemplos de uso (Normalmente se llamarían desde botones o un ViewModel)
        obtenerTareas()
        // crearNuevaTarea(Tarea(titulo = "Terminar servidor", descripcion = "Configurar Docker y base de datos"))
    }

    private fun obtenerTareas() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTareas()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val listaTareas = response.body()?.data
                        Log.d("CRUD", "Tareas obtenidas: ${listaTareas?.size}")
                        // Aquí pasarías 'listaTareas' a tu RecyclerView Adapter
                    } else {
                        mostrarError("Error del servidor: ${response.body()?.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarError("Excepción de red: ${e.message}")
                }
            }
        }
    }

    private fun crearNuevaTarea(tarea: Tarea) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.crearTarea(tarea)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@MainActivity, "Tarea Creada", Toast.LENGTH_SHORT).show()
                        obtenerTareas() // Refrescar la lista
                    } else {
                        mostrarError("No se pudo crear: ${response.body()?.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mostrarError("Excepción: ${e.message}")
                }
            }
        }
    }

    // Funciones similares para actualizarTarea() y eliminarTarea()...

    private fun mostrarError(mensaje: String) {
        Log.e("CRUD_ERROR", mensaje)
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
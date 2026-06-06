package com.example.app_test_uno

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // Variables para controlar la vista de la lista
    private lateinit var recyclerViewTareas: RecyclerView
    private lateinit var tareaAdapter: TareaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configurar el RecyclerView (La lista visual)
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas)
        recyclerViewTareas.layoutManager = LinearLayoutManager(this) // ESTO ES VITAL

        tareaAdapter = TareaAdapter(emptyList(),
            onEditarClick = { tareaSeleccionada ->
                Toast.makeText(this, "Editar en desarrollo: ${tareaSeleccionada.titulo}", Toast.LENGTH_SHORT).show()
            },
            onEliminarClick = { tareaSeleccionada ->
                Toast.makeText(this, "Eliminar en desarrollo: ${tareaSeleccionada.titulo}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerViewTareas.adapter = tareaAdapter

        // 2. Traer los datos iniciales
        obtenerTareas()

        // 3. Configurar el botón flotante
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarTarea)
        fabAgregar.setOnClickListener {
            mostrarDialogoCrearTarea()
        }
    }

    private fun mostrarDialogoCrearTarea() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tarea, null)
        val etTitulo = dialogView.findViewById<TextInputEditText>(R.id.etTitulo)
        val etDescripcion = dialogView.findViewById<TextInputEditText>(R.id.etDescripcion)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val titulo = etTitulo.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()

                if (titulo.isNotEmpty()) {
                    crearNuevaTarea(Tarea(titulo = titulo, descripcion = descripcion))
                } else {
                    mostrarError("El título es obligatorio")
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun obtenerTareas() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTareas()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Extraemos los datos y los mandamos al Adapter para pintarlos
                        val listaDesdeServidor = response.body()?.data ?: emptyList()
                        tareaAdapter.actualizarLista(listaDesdeServidor)
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
                        Toast.makeText(this@MainActivity, "Tarea Creada con éxito", Toast.LENGTH_SHORT).show()
                        // ¡MAGIA! Volvemos a pedir los datos a PHP para que la lista se recargue sola
                        obtenerTareas()
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

    private fun mostrarError(mensaje: String) {
        Log.e("CRUD_ERROR", mensaje)
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
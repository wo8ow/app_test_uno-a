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

    private lateinit var recyclerViewTareas: RecyclerView
    private lateinit var tareaAdapter: TareaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("BASE_URL", RetrofitClient.BASE_URL)
        Toast.makeText(
            this,
            "URL: ${RetrofitClient.BASE_URL}",
            Toast.LENGTH_LONG
        ).show()
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas)
        recyclerViewTareas.layoutManager = LinearLayoutManager(this)

        tareaAdapter = TareaAdapter(
            emptyList(),
            onEditarClick = { tareaSeleccionada ->
                Toast.makeText(
                    this,
                    "Editar en desarrollo: ${tareaSeleccionada.titulo}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onEliminarClick = { tareaSeleccionada ->
                Toast.makeText(
                    this,
                    "Eliminar en desarrollo: ${tareaSeleccionada.titulo}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        recyclerViewTareas.adapter = tareaAdapter

        obtenerTareas()

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarTarea)

        fabAgregar.setOnClickListener {
            mostrarDialogoCrearTarea()
        }
    }

    private fun mostrarDialogoCrearTarea() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tarea, null)

        val etTitulo = dialogView.findViewById<TextInputEditText>(R.id.etTitulo)
        val etDescripcion = dialogView.findViewById<TextInputEditText>(R.id.etDescripcion)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.setOnShowListener {
            val btnGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            btnGuardar.setOnClickListener {
                val titulo = etTitulo.text?.toString()?.trim().orEmpty()
                val descripcion = etDescripcion.text?.toString()?.trim().orEmpty()

                if (titulo.isEmpty()) {
                    etTitulo.error = "El título es obligatorio"
                    return@setOnClickListener
                }

                val nuevaTarea = CrearTareaRequest(
                    titulo = titulo,
                    descripcion = descripcion,
                    estado = "pendiente"
                )

                crearNuevaTarea(nuevaTarea)

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun obtenerTareas() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTareas()

                if (response.isSuccessful) {
                    val body = response.body()

                    Log.d("API_GET", "HTTP: ${response.code()}")
                    Log.d("API_GET", "success: ${body?.success}")
                    Log.d("API_GET", "message: ${body?.message}")
                    Log.d("API_GET", "cantidad: ${body?.data?.size ?: 0}")

                    withContext(Dispatchers.Main) {
                        if (body?.success == true) {
                            val listaDesdeServidor = body.data ?: emptyList()
                            tareaAdapter.actualizarLista(listaDesdeServidor)

                            if (listaDesdeServidor.isEmpty()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "No hay tareas registradas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            mostrarError("Error del servidor: ${body?.message}")
                        }
                    }
                } else {
                    val error = response.errorBody()?.string()

                    Log.e("API_GET", "HTTP error: ${response.code()}")
                    Log.e("API_GET", "errorBody: $error")

                    withContext(Dispatchers.Main) {
                        mostrarError("Error HTTP GET ${response.code()}: $error")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_GET", "Excepción GET", e)

                withContext(Dispatchers.Main) {
                    mostrarError("Excepción GET: ${e.message}")
                }
            }
        }
    }

    private fun crearNuevaTarea(tarea: CrearTareaRequest) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.crearTarea(tarea)

                if (response.isSuccessful) {
                    val body = response.body()

                    Log.d("API_POST", "HTTP: ${response.code()}")
                    Log.d("API_POST", "success: ${body?.success}")
                    Log.d("API_POST", "message: ${body?.message}")
                    Log.d("API_POST", "data: ${body?.data}")

                    withContext(Dispatchers.Main) {
                        if (body?.success == true) {
                            Toast.makeText(
                                this@MainActivity,
                                "Tarea creada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            obtenerTareas()
                        } else {
                            mostrarError("No se pudo crear: ${body?.message}")
                        }
                    }
                } else {
                    val error = response.errorBody()?.string()

                    Log.e("API_POST", "HTTP error: ${response.code()}")
                    Log.e("API_POST", "errorBody: $error")

                    withContext(Dispatchers.Main) {
                        mostrarError("Error HTTP POST ${response.code()}: $error")
                    }
                }
            } catch (e: Exception) {
                Log.e("API_POST", "Excepción POST", e)

                withContext(Dispatchers.Main) {
                    mostrarError("Excepción POST: ${e.message}")
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        Log.e("CRUD_ERROR", mensaje)
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
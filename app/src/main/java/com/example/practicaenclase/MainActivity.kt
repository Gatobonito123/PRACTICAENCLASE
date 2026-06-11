package com.example.practicaenclase

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.practicaenclase.Adapters.AlumnoAdapter
import com.example.practicaenclase.Models.Alumno
import com.example.practicaenclase.Models.Materia
import com.example.practicaenclase.Services.SupabaseManager
import com.example.practicaenclase.Utils.SupabaseErrorHandler
import com.google.android.material.progressindicator.CircularProgressIndicator
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerSemestre: Spinner
    private lateinit var spinnerMateria: Spinner
    private lateinit var lvAlumnos: ListView
    private lateinit var progressCarga: CircularProgressIndicator

    private var materias: List<Materia> = emptyList()
    private var ignorarSeleccionMateria = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerSemestre = findViewById(R.id.spinnerSemestre)
        spinnerMateria = findViewById(R.id.spinnerMateria)
        lvAlumnos = findViewById(R.id.lvAlumnos)
        progressCarga = findViewById(R.id.progressCarga)

        configurarSpinnerSemestre()
        configurarSpinnerMateria()
        configurarSpinnerMateriaInicial()

        spinnerSemestre.post {
            cargarMaterias(spinnerSemestre.selectedItemPosition + 1)
        }
    }

    private fun configurarSpinnerMateriaInicial() {
        actualizarSpinnerMaterias(listOf(getString(R.string.seleccione_materia)))
    }

    private fun configurarSpinnerSemestre() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.semestres,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerSemestre.adapter = adapter

        spinnerSemestre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarMaterias(position + 1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun configurarSpinnerMateria() {
        spinnerMateria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (ignorarSeleccionMateria) return
                if (position == 0 || materias.isEmpty()) {
                    lvAlumnos.adapter = null
                    return
                }
                cargarAlumnos()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun actualizarSpinnerMaterias(nombres: List<String>) {
        ignorarSeleccionMateria = true
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ArrayList(nombres)
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerMateria.adapter = adapter
        ignorarSeleccionMateria = false
    }

    private fun mostrarCarga(visible: Boolean) {
        progressCarga.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun cargarMaterias(nivel: Int) {
        lifecycleScope.launch {
            mostrarCarga(true)
            val nombresMaterias = mutableListOf(getString(R.string.seleccione_materia))

            try {
                materias = withContext(Dispatchers.IO) {
                    SupabaseManager.client
                        .from("materias")
                        .select {
                            filter {
                                eq("nivel", nivel)
                            }
                            order("nombre", Order.ASCENDING)
                        }
                        .decodeList<Materia>()
                }
                nombresMaterias.addAll(materias.map { it.nombre })
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@MainActivity, e)
                materias = emptyList()
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MainActivity, e)
                materias = emptyList()
            } finally {
                mostrarCarga(false)
            }

            actualizarSpinnerMaterias(nombresMaterias)
            lvAlumnos.adapter = null
        }
    }

    private fun cargarAlumnos() {
        lifecycleScope.launch {
            mostrarCarga(true)
            val listaAlumnos = ArrayList<Alumno>()

            try {
                val alumnos = withContext(Dispatchers.IO) {
                    SupabaseManager.client
                        .from("alumnos")
                        .select {
                            order("nombres", Order.ASCENDING)
                        }
                        .decodeList<Alumno>()
                }
                listaAlumnos.addAll(alumnos)
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            } finally {
                mostrarCarga(false)
            }

            lvAlumnos.adapter = AlumnoAdapter(this@MainActivity, listaAlumnos)
        }
    }
}

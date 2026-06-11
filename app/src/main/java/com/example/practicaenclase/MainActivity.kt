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
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerSemestre: Spinner
    private lateinit var spinnerMateria: Spinner
    private lateinit var lvAlumnos: ListView

    private var materias: List<Materia> = emptyList()
    private var semestreSeleccionado = false

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

        configurarSpinnerSemestre()
        configurarSpinnerMateria()
        configurarSpinnerMateriaInicial()
    }

    private fun configurarSpinnerMateriaInicial() {
        val placeholder = ArrayList<String>()
        placeholder.add(getString(R.string.seleccione_materia))
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            placeholder
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerMateria.adapter = adapter
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
                if (!semestreSeleccionado) {
                    semestreSeleccionado = true
                    return
                }
                cargarMaterias(position + 1)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun configurarSpinnerMateria() {
        spinnerMateria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0 || materias.isEmpty()) {
                    lvAlumnos.adapter = null
                    return
                }
                cargarAlumnos()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun cargarMaterias(nivel: Int) {
        lifecycleScope.launch {
            val nombresMaterias = ArrayList<String>()
            nombresMaterias.add(getString(R.string.seleccione_materia))

            try {
                materias = SupabaseManager.client
                    .from("materias")
                    .select {
                        filter {
                            eq("nivel", nivel)
                        }
                        order("nombre", Order.ASCENDING)
                    }
                    .decodeList<Materia>()

                nombresMaterias.addAll(materias.map { it.nombre })
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@MainActivity, e)
                materias = emptyList()
            }

            val adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                nombresMaterias
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinnerMateria.adapter = adapter
            lvAlumnos.adapter = null
        }
    }

    private fun cargarAlumnos() {
        lifecycleScope.launch {
            val listaAlumnos = ArrayList<Alumno>()

            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()

                listaAlumnos.addAll(alumnos)
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            }

            lvAlumnos.adapter = AlumnoAdapter(this@MainActivity, listaAlumnos)
        }
    }
}

package com.example.practicaenclase.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.practicaenclase.Models.Alumno
import com.example.practicaenclase.R

class AlumnoAdapter(
    context: Context,
    private val alumnos: ArrayList<Alumno>
) : ArrayAdapter<Alumno>(context, R.layout.item_alumno, alumnos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_alumno, parent, false)

        val alumno = alumnos[position]

        view.findViewById<TextView>(R.id.txtNombre).text = alumno.nombres
        view.findViewById<TextView>(R.id.txtCorreo).text = alumno.correo
        view.findViewById<TextView>(R.id.txtTelefono).text = alumno.telefono

        val fotoUrl = if (alumno.foto.startsWith("http")) {
            alumno.foto
        } else {
            "https://sga.uteq.edu.ec${alumno.foto}"
        }

        Glide.with(context)
            .load(fotoUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_action_name)
            .error(R.drawable.ic_action_name)
            .into(view.findViewById(R.id.imgAlumno))

        return view
    }
}

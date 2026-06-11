package com.example.practicaenclase.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.practicaenclase.Models.Resumen
import com.example.practicaenclase.R

class ResumenAdapter(context: Context, resource: Int, objects: List<Resumen>) :
    ArrayAdapter<Resumen>(context, resource, objects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val baseUrlImages = "https://uteq.edu.ec/assets/images/videos/res-sem/"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_resumen, parent, false)
        val item = getItem(position)

        val imgPortada = view.findViewById<ImageView>(R.id.imgPortada)
        val txtTitulo = view.findViewById<TextView>(R.id.txtTitulo)
        val txtFecha = view.findViewById<TextView>(R.id.txtFecha)
        val txtUrl = view.findViewById<TextView>(R.id.txtUrl)

        item?.let {
            txtTitulo.text = it.titulo
            txtFecha.text = "Publicado el: ${it.fechapub}"
            txtUrl.text = it.urlvideo1

            Glide.with(context)
                .load(baseUrlImages + it.portadaVideo)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imgPortada)
        }

        return view
    }
}

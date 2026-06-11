package com.example.practicaenclase.Utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.jan.supabase.exceptions.RestException

object SupabaseErrorHandler {

    fun show(context: Context, e: RestException) {
        MaterialAlertDialogBuilder(context)
            .setTitle(e.error ?: "Error de Supabase")
            .setMessage(e.description ?: e.message ?: "Ocurrió un error al consultar la base de datos.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}

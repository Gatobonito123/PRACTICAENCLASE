package com.example.practicaenclase.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.jan.supabase.exceptions.RestException

object SupabaseErrorHandler {

    private const val TAG = "SupabaseError"

    fun show(context: Context, e: RestException) {
        Log.e(TAG, "RestException: ${e.error} - ${e.description}", e)
        MaterialAlertDialogBuilder(context)
            .setTitle(e.error ?: "Error de Supabase")
            .setMessage(e.description ?: e.message ?: "Ocurrió un error al consultar la base de datos.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    fun show(context: Context, e: Exception) {
        Log.e(TAG, "Exception: ${e.message}", e)
        Toast.makeText(
            context,
            "Error: ${e.message ?: "No se pudo conectar con Supabase"}",
            Toast.LENGTH_LONG
        ).show()
    }
}

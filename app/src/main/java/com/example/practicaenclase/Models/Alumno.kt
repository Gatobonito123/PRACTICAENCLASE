package com.example.practicaenclase.Models

import kotlinx.serialization.Serializable

@Serializable
data class Alumno(
    val id: Int,
    val nombres: String,
    val correo: String,
    val telefono: String,
    val foto: String
)

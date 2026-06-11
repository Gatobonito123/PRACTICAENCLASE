package com.example.practicaenclase.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Alumno(
    val id: Int,
    val nombres: String,
    val correo: String,
    val telefono: String,
    val foto: String,
    val paralelo: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

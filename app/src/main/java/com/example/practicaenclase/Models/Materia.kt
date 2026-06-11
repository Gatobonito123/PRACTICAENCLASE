package com.example.practicaenclase.Models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Materia(
    val id: Int,
    val nombre: String,
    val nivel: Int,
    @SerialName("created_at") val createdAt: String? = null
)

package com.example.practicaenclase.Models

import kotlinx.serialization.Serializable

@Serializable
data class Resumen(
    val fechapub: String,
    val titulo: String,
    val urlvideo1: String,
    val portadaVideo: String
)

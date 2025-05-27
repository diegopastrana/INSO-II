package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Videojuego(val id: Int, val nombre: String, val precio: Double, val descripcion: String)

@Serializable
data class OrdenRequest(val videojuegoId: Int, val cantidad: Int, val token: String)

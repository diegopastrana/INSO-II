package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CarritoItem(
    val videojuegoId: Int,
    val nombre: String,
    val precioUnitario: Double,
    val cantidad: Int
)

@Serializable
data class CarritoRequest(
    val videojuegoId: Int,
    val cantidad: Int
)
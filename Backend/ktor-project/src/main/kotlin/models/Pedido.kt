package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Pedido(
    val id: Int,
    val total: Double,
    val estado: String,
    val fecha: String,
    val items: List<PedidoItem>
)

@Serializable
data class PedidoItem(
    val videojuegoId: Int,
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double
)
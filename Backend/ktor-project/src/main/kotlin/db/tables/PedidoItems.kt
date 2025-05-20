package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object PedidoItems : Table("pedido_items") {
    val pedidoId = reference("pedido_id", Pedidos)
    val videojuegoId = reference("videojuego_id", Videojuegos)
    val cantidad = integer("cantidad")
    val precioUnitario = decimal("precio_unitario", 10, 2)
}
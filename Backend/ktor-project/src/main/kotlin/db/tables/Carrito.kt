package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object Carrito : Table("carrito") {
    val usuarioId = varchar("usuario_id", 100).references(Usuarios.id)
    val videojuegoId = reference("videojuego_id", Videojuegos)
    val cantidad = integer("cantidad")

    override val primaryKey = PrimaryKey(usuarioId, videojuegoId)
}
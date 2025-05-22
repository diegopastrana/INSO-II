package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Videojuegos : IntIdTable("videojuegos") {
    val nombre = varchar("nombre", 255)
    val precio = decimal("precio", 10,2)
    val description = text("description")
}
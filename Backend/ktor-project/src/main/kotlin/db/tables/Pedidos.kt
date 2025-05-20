package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Pedidos : IntIdTable("pedidos") {
    val usuarioId = varchar("usuario_id", 100)
    val total = decimal("total", 10, 2)
    val estado = varchar("estado", 50) // ejemplo: "pendiente", "pagado"
    val fecha = datetime("fecha")
}
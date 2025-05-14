package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object Usuarios : Table("usuarios") {
    val id = varchar("id", 100) // Google ID
    val nombre = varchar("nombre", 255)
    val email = varchar("email", 255)
    val avatar = varchar("avatar", 500)
    val rol = varchar("rol", 50).default("cliente")
    override val primaryKey = PrimaryKey(id)
}
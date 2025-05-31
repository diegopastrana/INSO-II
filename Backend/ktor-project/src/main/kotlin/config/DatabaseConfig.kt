package com.example.config

import org.jetbrains.exposed.sql.Database
import java.net.URI

fun initDatabase() {

    val databaseUrl = System.getenv("DATABASE_URL")
        ?: throw IllegalStateException("La variable de entorno DATABASE_URL no está definida")

    val uri = URI(databaseUrl)

    val (username, password) = uri.userInfo
        .split(":", limit = 2)
        .let {
            if (it.size != 2) {
                throw IllegalStateException("Formato inválido de DATABASE_URL: falta usuario o contraseña")
            }
            it[0] to it[1]
        }

    val host = uri.host
    val port = if (uri.port == -1) 5432 else uri.port

    val databaseName = uri.path.removePrefix("/")

    val jdbcUrl = "jdbc:postgresql://$host:$port/$databaseName?sslmode=require"

    Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver",
        user = username,
        password = password
    )
}

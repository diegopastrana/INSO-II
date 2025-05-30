package com.example.config

import io.ktor.server.util.url
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    // 1. Raw = "postgres://user:pass@host:5432/dbname"
    val rawUrl = System.getenv("DATABASE_URL")
        ?: error("DATABASE_URL no está definida")

    // 2. Con la forma JDBC
    val jdbcUrl = rawUrl
        .removePrefix("postgres://")           // "user:pass@host:5432/dbname"
        .let { credsAndHost ->
            val (creds, hostAndDb) = credsAndHost.split("@", limit = 2)
            // creds = "user:pass", hostAndDb = "host:5432/dbname"
            "jdbc:postgresql://$hostAndDb?sslmode=require&user=${creds.substringBefore(":")}&password=${creds.substringAfter(":")}"
        }

    // 3. Conectar
    Database.connect(
        url    = jdbcUrl,
        driver = "org.postgresql.Driver"
    )
}





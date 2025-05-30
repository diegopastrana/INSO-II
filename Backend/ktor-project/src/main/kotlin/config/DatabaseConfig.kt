package com.example.config

import io.ktor.server.util.url
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    val raw = System.getenv("DATA_BASE_URL")!!
    val jdbcUrl = if (raw.startsWith("jdbc:")) raw else "jdbc:$raw"
    Database.connect(
        url      = jdbcUrl,
        driver   = "org.postgresql.Driver",
        user     = System.getenv("DB_USER")!!,
        password = System.getenv("DB_PASSWORD")!!
    )
}

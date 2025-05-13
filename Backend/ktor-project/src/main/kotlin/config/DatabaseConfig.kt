package com.example.config

import io.ktor.server.util.url
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    Database.connect(
        url = "jdbc:postgresql://${System.getenv("DB_HOST")}:${System.getenv("DB_PORT")}/${System.getenv("DB_NAME")}",
        driver = "org.postgresql.Driver",
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD")
    )
}
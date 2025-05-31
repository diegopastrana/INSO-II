package com.example.config

import io.ktor.server.util.url
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    Database.connect("jdbc:${System.getenv("DATABASE_URL")}?sslmode=require", "org.postgresql.Driver")
}





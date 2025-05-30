package com.example.config

import io.ktor.server.util.url
import org.jetbrains.exposed.sql.Database

fun initDatabase() {
    val host     = System.getenv("DB_HOST")!!
    val port     = System.getenv("DB_PORT")!!
    val dbName   = System.getenv("DB_NAME")!!
    val user     = System.getenv("DB_USER")!!
    val password = System.getenv("DB_PASSWORD")!!

    val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName?sslmode=require"

    Database.connect(
        url      = jdbcUrl,
        driver   = "org.postgresql.Driver",
        user     = user,
        password = password
    )
}




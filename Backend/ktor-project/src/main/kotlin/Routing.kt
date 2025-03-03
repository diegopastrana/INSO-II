package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/videojuegos") {
            val juegos = transaction {
                Videojuegos.selectAll().map {
                    Videojuego(
                        id = it[Videojuegos.id],
                        nombre = it[Videojuegos.nombre],
                        precio = it[Videojuegos.precio].toDouble(),
                        descripcion = it[Videojuegos.descripcion]
                    )
                }
            }
            call.respond(juegos)
        }
    }
}

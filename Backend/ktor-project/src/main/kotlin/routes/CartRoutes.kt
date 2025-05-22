package com.example.routes

import com.example.db.tables.Carrito
import com.example.db.tables.Videojuegos
import com.example.models.CarritoItem
import com.example.models.CarritoRequest
import com.example.models.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

// TODO cambiar el session por el JWT token

fun Application.configureCartRoutes() {
    routing {
        authenticate("auth-jwt") {
            get("/api/cart") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.subject ?: return@get call.respond(HttpStatusCode.BadRequest)

                val items = transaction {
                    (Carrito innerJoin Videojuegos)
                        .selectAll().where(Carrito.usuarioId eq userId)
                        .map {
                            CarritoItem(
                                videojuegoId = it[Videojuegos.id].value,
                                nombre = it[Videojuegos.nombre],
                                precioUnitario = it[Videojuegos.precio].toDouble(),
                                cantidad = it[Carrito.cantidad]
                            )
                        }
                }

                call.respond(items)
            }

            post("/api/cart") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val userId = principal.subject ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<CarritoRequest>()

                transaction {
                    val existe = Carrito.selectAll()
                        .where((Carrito.usuarioId eq userId) and (Carrito.videojuegoId eq request.videojuegoId))
                        .count() > 0

                    if (existe) {
                        Carrito.update({
                            (Carrito.usuarioId eq userId) and (Carrito.videojuegoId eq request.videojuegoId)
                        }) {
                            it[cantidad] = request.cantidad
                        }
                    } else {
                        Carrito.insert {
                            it[usuarioId] = userId
                            it[videojuegoId] = request.videojuegoId
                            it[cantidad] = request.cantidad
                        }
                    }
                }

                call.respond(HttpStatusCode.OK, "Producto agregado al carrito")
            }

            put("/api/cart") {
                val session = call.sessions.get<UserSession>() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<CarritoRequest>()

                val actualizado = transaction {
                    Carrito.update({
                        (Carrito.usuarioId eq session.userId) and (Carrito.videojuegoId eq request.videojuegoId)
                    }) {
                        it[cantidad] = request.cantidad
                    }
                }

                if (actualizado > 0)
                    call.respond(HttpStatusCode.OK, "Carrito actualizado")
                else
                    call.respond(HttpStatusCode.NotFound, "Item no encontrado en el carrito")
            }

            delete("/api/cart/{gameId}") {
                val session =
                    call.sessions.get<UserSession>() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val gameId = call.parameters["gameId"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID inválido")

                val eliminado = transaction {
                    Carrito.deleteWhere {
                        (Carrito.usuarioId eq session.userId) and (Carrito.videojuegoId eq gameId)
                    }
                }

                if (eliminado > 0)
                    call.respond(HttpStatusCode.OK, "Producto eliminado")
                else
                    call.respond(HttpStatusCode.NotFound, "Producto no encontrado")
            }
        }
    }
}
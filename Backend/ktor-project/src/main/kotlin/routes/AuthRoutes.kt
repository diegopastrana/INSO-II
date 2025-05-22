package com.example.routes

import com.example.db.tables.Usuarios
import com.example.models.UserSession
import com.example.models.Usuario
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureAuthRoutes() {
    routing {
        authenticate("auth-jwt") {
            get ("/api/users/me") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Token inválido")

                val userId = principal.subject ?: return@get call.respond(HttpStatusCode.BadRequest, "Token sin subject")

                val usuario = transaction {
                    Usuarios.selectAll()
                        .where (Usuarios.id eq userId )
                        .map {
                            Usuario(
                                id = it[Usuarios.id],
                                nombre = it[Usuarios.nombre],
                                email = it[Usuarios.email],
                                avatar = it[Usuarios.avatar]
                            )
                        }
                        .singleOrNull()
                }

                if (usuario == null) {
                    call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                } else {
                    call.respond(usuario)
                }
            }

            get("/api/users/{id}") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Token inválido")

                val userId = principal.subject ?: return@get call.respond(HttpStatusCode.BadRequest, "Token sin subject")
                // Esta ruta esta reservada para admins

                call.respond("Obtiene los datos de un usuario especifico")
            }

            get("/api/users") {
                val usuarios = transaction {
                    Usuarios.selectAll().map {
                        Usuario(
                            id = it[Usuarios.id],
                            nombre = it[Usuarios.nombre],
                            avatar = it[Usuarios.avatar],
                            email = it[Usuarios.email]
                        )
                    }
                }
                call.respond(usuarios)
            }

            post("/api/auth/logout") {
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.OK, "Sesión cerrada")
            }
        }
    }
}
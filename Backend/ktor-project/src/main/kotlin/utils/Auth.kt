package com.example.utils

import com.example.db.tables.Usuarios
import com.example.models.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun ApplicationCall.getCurrentUserRole(): String? {
    val session = sessions.get<UserSession>() ?: return null
    return transaction {
        Usuarios.selectAll()
            .where(Usuarios.id eq session.userId )
            .map { it[Usuarios.rol] }
            .singleOrNull()
    }
}

suspend fun ApplicationCall.requireRole(required: String): Boolean {
    val role = getCurrentUserRole()
    return if (role != required) {
        respond(HttpStatusCode.Forbidden, "No tienes permiso para acceder")
        false
    } else true
}
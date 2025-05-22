package com.example.routes

import com.example.db.tables.Videojuegos
import com.example.models.UserSession
import com.example.models.Videojuego
import com.example.utils.PageLinks
import com.example.utils.PaginatedResponse
import com.example.utils.requireRole
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.parameters
import io.ktor.server.application.Application
import io.ktor.server.plugins.origin
import io.ktor.server.request.host
import io.ktor.server.request.path
import io.ktor.server.request.port
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureGameRoutes() {
    routing {
        get("/api/games") {
            val page = call.request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull()?.coerceIn(1,100) ?: 20
            val search = call.request.queryParameters["s"]?.takeIf { it.isNotBlank() }?.trim()
            val offset = (page - 1) * size

            val filter: (org.jetbrains.exposed.sql.Query.() -> Unit)? = search?.let { term ->
                { andWhere { Videojuegos.nombre.lowerCase() like "%${term.lowercase()}%" } }
            }

            val (juegos, total) = transaction {
                val baseQuery = Videojuegos.selectAll().apply {
                    filter?.invoke(this)
                }
                val cnt = baseQuery.count()
                val list = baseQuery
                    .limit(size)
                    .offset(offset.toLong())
                    .map { row ->
                    Videojuego(
                        id = row[Videojuegos.id].value,
                        nombre = row[Videojuegos.nombre],
                        precio = row[Videojuegos.precio].toDouble(),
                        descripcion = row[Videojuegos.description]
                    )
                }
                list to cnt
            }

            val lastPage = ((total + size - 1) / size).toInt()

            val baseUrl = URLBuilder().apply {
                protocol = call.request.origin.scheme.takeIf { it == "https" }?.let { URLProtocol.HTTPS } ?: URLProtocol.HTTP
                host = call.request.host()
                port = call.request.port()
                encodedPath = call.request.path()
            }.buildString()

            fun makeLink(p: Int) = URLBuilder(baseUrl).apply {
                if (search !== null)
                    parameters.append("s", search)
                parameters.append("page", p.toString())
                if (size != 20)
                    parameters.append("size", size.toString())
            }.buildString()

            val lasPage = ((total -1) / size + 1).toInt()

            val links = PageLinks(
                first = makeLink(1),
                self = makeLink(page),
                next = if(page < lasPage) makeLink(page + 1) else null,
                prev = if(page > 1) makeLink(page - 1) else null,
                last  = makeLink(lastPage)
            )

            call.respond(
                PaginatedResponse(
                    page=page,
                    size=size,
                    total=total,
                    items=juegos,
                    links=links
                )
            )
        }
    }
}
package com.example.routes

import com.example.db.tables.Carrito
import com.example.db.tables.PedidoItems
import com.example.db.tables.Pedidos
import com.example.db.tables.Videojuegos
import com.example.models.Pedido
import com.example.models.PedidoItem
import com.example.models.UserSession
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
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
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

fun Application.configureOrderRoutes() {
    routing {
        post("/api/checkout") {
            val session = call.sessions.get<UserSession>() ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val items = transaction {
                (Carrito innerJoin Videojuegos).select ( Carrito.usuarioId eq session.userId ).map {
                    Triple(
                        it[Videojuegos.id],
                        it[Carrito.cantidad],
                        it[Videojuegos.precio]
                    )
                }
            }

            if (items.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "El carrito está vacío")
                return@post
            }

            val total = items.sumOf { it.third  * it.second.toBigDecimal() }

            val pedidoId = transaction {
                val id = Pedidos.insertAndGetId {
                    it[usuarioId] = session.userId
                    it[Pedidos.total] = total
                    it[estado] = "pendiente"
                    it[fecha] = LocalDateTime.now()
                }.value

                items.forEach { (videojuegoId, cantidad, precio) ->
                    PedidoItems.insert {
                        it[PedidoItems.pedidoId] = id
                        it[PedidoItems.videojuegoId] = videojuegoId
                        it[PedidoItems.cantidad] = cantidad
                        it[PedidoItems.precioUnitario] = precio
                    }
                }

                Carrito.deleteWhere { Carrito.usuarioId eq session.userId }

                id
            }

            // Simular pago
            call.respond(mapOf(
                "mensaje" to "Pedido creado con ID $pedidoId. Esperando confirmación de pago.",
                "pedidoId" to pedidoId
            ))
        }

        post("/api/stripe/webhook") {
            val secret = System.getenv("STRIPE_WEBHOOK_SECRET") ?: return@post call.respond(HttpStatusCode.InternalServerError)

            val payload = call.receiveText()
            val sigHeader = call.request.headers["Stripe-Signature"]

            val event: Event = try {
                Webhook.constructEvent(
                    payload,
                    sigHeader,
                    secret
                )
            } catch (e: SignatureVerificationException) {
                call.respond(HttpStatusCode.BadRequest, "Firma inválida")
                return@post
            }

            if (event.type == "checkout.session.completed") {
                val deserializedObject = event.dataObjectDeserializer.deserializeUnsafe() as Session
                val pedidoId = deserializedObject.metadata?.get("pedido_id")?.toIntOrNull()

                if (pedidoId != null) {
                    transaction {
                        Pedidos.update({ Pedidos.id eq pedidoId }) {
                            it[estado] = "pagado"
                        }
                    }
                }
            }

            call.respond(HttpStatusCode.OK)
        }

        get("/api/orders") {
            val session = call.sessions.get<UserSession>() ?: return@get call.respond(HttpStatusCode.Unauthorized)

            val pedidos = transaction {
                Pedidos.select ( Pedidos.usuarioId eq session.userId ).map {
                    Pedido(
                        id = it[Pedidos.id].value,
                        total = it[Pedidos.total].toDouble(),
                        estado = it[Pedidos.estado],
                        fecha = it[Pedidos.fecha].toString(),
                        items = emptyList() // Podrías incluirlos si quieres
                    )
                }
            }

            call.respond(pedidos)
        }

        get("/api/orders/{id}") {
            val session = call.sessions.get<UserSession>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "ID inválido")

            val pedido = transaction {
                val pedidoBase = Pedidos.selectAll()
                    .where((Pedidos.id eq id) and (Pedidos.usuarioId eq session.userId))
                    .singleOrNull() ?: return@transaction null

                val items = (PedidoItems innerJoin Videojuegos).selectAll()
                    .where(PedidoItems.pedidoId eq id)
                    .map {
                    PedidoItem(
                        videojuegoId = it[Videojuegos.id].value,
                        nombre = it[Videojuegos.nombre],
                        cantidad = it[PedidoItems.cantidad],
                        precioUnitario = it[PedidoItems.precioUnitario].toDouble()
                    )
                }

                Pedido(
                    id = pedidoBase[Pedidos.id].value,
                    total = pedidoBase[Pedidos.total].toDouble(),
                    estado = pedidoBase[Pedidos.estado],
                    fecha = pedidoBase[Pedidos.fecha].toString(),
                    items = items
                )
            }

            if (pedido == null) {
                call.respond(HttpStatusCode.NotFound, "Pedido no encontrado")
            } else {
                call.respond(pedido)
            }
        }

        put("/api/orders") {
            call.respondText("Actualiza el estado del pedido")
        }
    }
}
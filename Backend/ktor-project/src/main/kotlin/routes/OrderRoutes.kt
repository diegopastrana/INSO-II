package com.example.routes

import com.example.db.tables.Carrito
import com.example.db.tables.PedidoItems
import com.example.db.tables.Pedidos
import com.example.db.tables.Usuarios
import com.example.db.tables.Videojuegos
import com.example.models.Pedido
import com.example.models.PedidoItem
import com.example.models.UserSession
import com.stripe.Stripe
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
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
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

fun Application.configureOrderRoutes() {
    routing {
        authenticate("auth-jwt") {
            post("/api/create-payment-intent") {
                val principal = call.principal<JWTPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val userId = principal.subject ?: return@post call.respond(HttpStatusCode.BadRequest)

                val email: String? = transaction {
                    Usuarios
                        .selectAll()
                        .where(Usuarios.id eq userId )
                        .map { it[Usuarios.email] }
                        .firstOrNull()
                }

                print(email)

                val total = transaction {
                    (Carrito innerJoin Videojuegos)
                        .selectAll()
                        .where(Carrito.usuarioId eq userId)
                        .sumOf {
                            it[Videojuegos.precio] * it[Carrito.cantidad].toBigDecimal()
                        }
                }

                if (total == BigDecimal.ZERO) {
                    return@post call.respond(HttpStatusCode.BadRequest, "El carrito está vacío")
                }

                Stripe.apiKey = System.getenv("STRIPE_API_KEY")!!

                val params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.AMAZON_PAY)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                    .setCustomerEmail(email)
                    .addLineItem(
                        SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("eur")
                                    .setUnitAmount((total * 100.toBigDecimal()).toLong())
                                    .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Tu carrito")
                                            .build()
                                    )
                                    .build()
                            )
                            .setQuantity(1L)
                            .build()
                    )
                    .setUiMode(SessionCreateParams.UiMode.CUSTOM)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .build()

                val session = Session.create(params)

                print(session)

                // Devuelve el secret de la sesión (prefijo cs_)
                call.respond(mapOf("clientSecret" to session.clientSecret))
            }

            get("/api/orders") {
                val session = call.sessions.get<UserSession>() ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val pedidos = transaction {
                    Pedidos.select(Pedidos.usuarioId eq session.userId).map {
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
}
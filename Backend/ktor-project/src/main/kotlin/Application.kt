package com.example

import com.example.config.configureSecurity
import com.example.config.configureSerialization
import com.example.config.initDatabase
import com.example.db.seed.insertInitialGames
import com.example.db.tables.Carrito
import com.example.db.tables.PedidoItems
import com.example.db.tables.Pedidos
import com.example.db.tables.Usuarios
import com.example.db.tables.Videojuegos
import com.example.routes.configureAuthRoutes
import com.example.routes.configureCartRoutes
import com.example.routes.configureGameRoutes
import com.example.routes.configureOrderRoutes
import com.example.routes.configureTestRoutes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.netty.handler.codec.DefaultHeaders
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
  io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
  configureSerialization()
  initDatabase()

  transaction {
    SchemaUtils.create(
      Videojuegos,
      Usuarios,
      Pedidos,
      PedidoItems,
      Carrito
    )
  }

  intercept(ApplicationCallPipeline.Setup) {
    call.response.headers.append(
      "Content-Security-Policy",
      //http://localhost:8080
      //https://inso-ii.onrender.com
      "default-src 'self'; connect-src 'self' https://inso-ii.onrender.com"
    )
  }

  install(CORS) {
    anyHost() // NOTE: restrict in production
    allowCredentials = true
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.Authorization)
    allowHeader(HttpHeaders.Cookie)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
  }

  insertInitialGames()
  configureSecurity()
  configureTestRoutes()
  configureAuthRoutes()
  configureGameRoutes()
  configureCartRoutes()
  configureOrderRoutes()
}

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
import io.ktor.server.application.Application
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

    insertInitialGames()
    configureSecurity()
    configureTestRoutes()
    configureAuthRoutes()
    configureGameRoutes()
    configureCartRoutes()
    configureOrderRoutes()
}

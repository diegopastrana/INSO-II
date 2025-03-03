package com.example

import io.ktor.server.application.*
import kotlinx.serialization.Serializable

import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils

fun initDatabase() {
    Database.connect(
        url="jdbc:mysql://localhost:3306/kotlin_project?useSSL=false&serverTimezone=UTC",
        driver="com.mysql.cj.jdbc.Driver",
        user="kt_user",
        password="kt_pass"
    )
}

object Videojuegos : Table("videojuegos") {
    val id = integer("id").autoIncrement()
    val nombre = varchar("nombre", 255)
    val precio = decimal("precio", 10, 2)
    val descripcion = text("descripcion")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
class Videojuego(val id: Int, val nombre: String, val precio: Double, val descripcion: String)

@Serializable
data class OrderRequest(val videojuegoId: Int, val cantidad: Int, val token: String)


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
}

fun Application.module() {
    configureSerialization()

    initDatabase()
    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Videojuegos)
    }
    configureSecurity()
    configureRouting()
}

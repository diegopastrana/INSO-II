package com.example.db.seed

import com.example.db.tables.Videojuegos
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

@Serializable
data class GameSeed(
  val nombre: String,
  val precio: Double,
  val descripcion: String,
  val cover: String,
  val genero: String
)

fun insertInitialGames() {
  val resource = Thread
    .currentThread()
    .contextClassLoader
    .getResource("seed/games.json")
    ?: throw IllegalStateException("No se encontró seed/games.json en resources")
  val gamesText = File(resource.toURI()).readText()

  val json = Json { ignoreUnknownKeys = true }
  val games: List<GameSeed> = json.decodeFromString(gamesText)

  transaction {
    if (Videojuegos.selectAll().empty()) {
      println("Insertando ${games.size} videojuegos iniciales...")
      games.forEach { g ->
        Videojuegos.insert {
          it[nombre] = g.nombre
          it[precio] = g.precio.toBigDecimal()
          it[description] = g.descripcion
          it[cover] = g.cover
          it[genero] = g.genero
        }
        println("Juego Insertado")
      }
      println("Seed completed.")
    } else {
      println("La tabla Videojuegos ya contiene datos, seed omitido.")
    }
  }
}

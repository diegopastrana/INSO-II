package com.example.db.seed

import com.example.db.tables.Videojuegos
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class GameSeed(
  val nombre: String,
  val precio: Double,
  val descripcion: String,
  val cover: String,
  val genero: String
)

@OptIn(ExperimentalSerializationApi::class)
fun insertInitialGames() {
  val resource = Thread
    .currentThread()
    .contextClassLoader
    .getResourceAsStream("seed/games.json")
    ?: throw IllegalStateException("No se encontró seed/games.json en resources")

  val json = Json { ignoreUnknownKeys = true }
  val games: List<GameSeed> = json.decodeFromStream(resource)

  // 3) Insertar en la BD si la tabla está vacía
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

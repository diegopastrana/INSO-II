package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.db.tables.Usuarios
import com.example.models.Usuario
import com.example.utils.JwtService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.oauth
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureSecurity() {
    install(Authentication) {
        oauth("auth-oauth-google") {
            val apiBaseUrl = System.getenv("RENDER_EXTERNAL_URL") ?: error("API_URL no configurada")
            val clientId = System.getenv("GOOGLE_CLIENT_ID") ?: error("GOOGLE_CLIENT_ID no configurada")
            val clientSecret = System.getenv("GOOGLE_CLIENT_SECRET") ?: error("GOOGLE_CLIENT_SECRET no configurada")

            urlProvider = { "$apiBaseUrl/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://oauth2.googleapis.com/token",
                    requestMethod = HttpMethod.Post,
                    clientId = clientId,
                    clientSecret = clientSecret,
                    defaultScopes = listOf("openid", "profile", "email")
                )
            }
            client = HttpClient(CIO)
        }

        jwt("auth-jwt") {
            val jwtSecret = System.getenv("JWT_SECRET") ?: error("JWT_SECRET no configurada")

            realm = "ktor-app"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer("ktor-api")
                    .withAudience("ktor-users")
                    .build()
            )
            validate { credential ->
                credential.payload.subject?.let { JWTPrincipal(credential.payload) }
            }
            authHeader {
                it.request.cookies["AUTH_TOKEN"]?.let { token ->
                    HttpAuthHeader.Single("Bearer", token)
                }
            }
        }
    }

    routing {
        authenticate("auth-oauth-google") {
            get("/login") {}

            get("/callback") {
                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                    ?: return@get call.respondText("Auth failed", status = HttpStatusCode.Unauthorized)

                val profile = fetchGoogleProfile(principal.accessToken)

                transaction {
                    Usuarios.insertIgnore {
                        it[id] = profile.id
                        it[nombre] = profile.nombre
                        it[email] = profile.email
                        it[avatar] = profile.avatar
                    }
                }

                val jwt = JwtService.generateToken(profile.id, profile.email)

                val frontendUrlFull = System.getenv("FRONTEND_URL")
                    ?: throw IllegalStateException("No se ha encontrado la variable de entorno FRONTEND_URL")

                val frontendHost = try {
                    val uri = java.net.URI.create(frontendUrlFull)
                    uri.host // p. ej. "mi-front-end.onrender.com"
                } catch (e: Exception) {
                    throw IllegalStateException("FRONTEND_URL no es una URI válida: $frontendUrlFull")
                }
                call.response.cookies.append(
                    Cookie(
                        name = "AUTH_TOKEN",
                        value = jwt,
                        httpOnly = true,
                        domain = "onrender.com",
                        secure = false,
                        extensions = mapOf("SameSite" to "Lax"),
                        maxAge = 60 * 60 * 24
                    )
                )

                call.respondRedirect(frontendUrlFull)
            }
        }
    }
}

private suspend fun fetchGoogleProfile(accessToken: String): Usuario {
    val client = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val response: Usuario = client.get("https://www.googleapis.com/oauth2/v2/userinfo") {
        headers {
            append(HttpHeaders.Authorization, "Bearer $accessToken")
        }
    }.body()

    client.close()
    return response
}

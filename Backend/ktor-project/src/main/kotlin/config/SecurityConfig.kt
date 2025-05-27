package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.db.tables.Usuarios
import com.example.models.UserSession
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
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureSecurity() {
    install(Authentication) {
        oauth("auth-oauth-google") {
            //http:localhost:8080
            urlProvider = { "https://inso-ii.onrender.com/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://oauth2.googleapis.com/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("openid", "profile", "email")
                )
            }
            client = HttpClient(CIO)
        }


        jwt("auth-jwt") {
            realm = "ktor-app"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(System.getenv("JWT_SECRET") ?: "dev-secret"))
                    .withIssuer("ktor-api")
                    .withAudience("ktor-users")
                    .build()
            )
            validate { credential ->
                if (credential.payload.subject != null) JWTPrincipal(credential.payload) else null
            }

            authHeader {
                // Extraer token desde cookie
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
                if (principal == null) {
                    call.respondText("Authentication failed", status = HttpStatusCode.Unauthorized)
                    return@get
                }

                // Obtener perfil desde Google
                val profile = fetchGoogleProfile(principal.accessToken)

                // Guardar o actualizar usuario
                transaction {
                    Usuarios.insertIgnore {
                        it[id] = profile.id
                        it[nombre] = profile.nombre
                        it[email] = profile.email
                        it[avatar] = profile.avatar
                    }
                }

                val jwt = JwtService.generateToken(userId = profile.id, email = profile.email)

                call.response.cookies.append(
                    Cookie(
                        name = "AUTH_TOKEN",
                        value = jwt,
                        domain = "localhost",
                        path = "/",
                        httpOnly = true,
                        secure = false, // Cambia a false en local sin HTTPS
                        extensions = mapOf("SameSite" to "Lax"),
                        maxAge = 60 * 60 * 24
                    )
                )

                call.respondRedirect("/api/users/me")
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

package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date

object JwtService {
    private const val issuer = "ktor-api"
    private const val audience = "ktor-users"
    private val secret = System.getenv("JWT_SECRET") ?: "dev-secret"
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: String, email: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .sign(algorithm)
    }

    fun decodeToken(token: String): DecodedJWT {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
            .verify(token)
    }

    fun verifyToken(token: String): Boolean {
        return try {
            decodeToken(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }
}
package com.example.routes

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureTestRoutes() {
    routing {
        get("/health") {
            call.respond("The API is up and healthy")
        }
    }
}
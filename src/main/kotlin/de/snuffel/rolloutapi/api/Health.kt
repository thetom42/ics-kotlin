package de.snuffel.rolloutapi.api

import io.ktor.application.*
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.Route

fun Route.health() {
    get("/health") {
        call.respond(mapOf("status" to "healthy"))
    }
}

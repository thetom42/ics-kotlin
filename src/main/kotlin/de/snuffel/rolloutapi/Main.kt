package de.snuffel.rolloutapi

import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import de.snuffel.rolloutapi.api.persist
import de.snuffel.rolloutapi.api.report
import de.snuffel.rolloutapi.api.health
import de.snuffel.rolloutapi.db.Database

fun main() {
    Database.init()

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (cause.message ?: "Unknown error")))
            }
        }
        routing {
            persist()
            report()
            health()
        }
    }.start(wait = true)
}

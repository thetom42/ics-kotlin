package de.snuffel.rolloutapi.api

import io.ktor.application.*
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.Route
import de.snuffel.rolloutapi.db.Database
import de.snuffel.rolloutapi.models.Rollout
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

fun Route.report() {
    get("/report") {
        val startDate = call.request.queryParameters["start_date"]
        val endDate = call.request.queryParameters["end_date"]

        if (startDate == null || endDate == null) {
            call.respond(mapOf("error" to "Missing query parameters"))
            return@get
        }

        val rollouts = Database.getRolloutsByDateRange(startDate, endDate)
        val rolloutObjects = rollouts.map { Json.decodeFromString<Rollout>(it) }

        call.respond(rolloutObjects)
    }
}

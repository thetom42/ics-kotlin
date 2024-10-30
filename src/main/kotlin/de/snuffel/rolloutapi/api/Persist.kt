package de.snuffel.rolloutapi.api

import io.ktor.application.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.Route
import de.snuffel.rolloutapi.db.Database
import de.snuffel.rolloutapi.models.Rollout
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

fun Route.persist() {
    post("/persist") {
        val rollouts = call.receive<List<Rollout>>()
        rollouts.forEach { rollout ->
            val existingRecord = Database.getRolloutById(rollout.id)
            if (existingRecord == null) {
                val rolloutJson = Json.encodeToString(rollout)
                Database.insertRollout(rollout.id, rolloutJson)
            }
        }
        call.respond(mapOf("status" to "success"))
    }
}

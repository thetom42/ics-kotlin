package de.snuffel.rolloutapi.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import de.snuffel.rolloutapi.db.Database
import de.snuffel.rolloutapi.models.Rollout
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class ReportTest : StringSpec({
    "should return rollout objects within the date range" {
        val databaseMock = mockk<Database>()
        val rollout1 = Rollout("1", mapOf("key" to "value1"))
        val rollout2 = Rollout("2", mapOf("key" to "value2"))
        val rollouts = listOf(rollout1, rollout2)

        every { databaseMock.getRolloutsByDateRange("2021-01-01", "2021-12-31") } returns rollouts.map { Json.encodeToString(it).toByteArray() }

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/report?start_date=2021-01-01&end_date=2021-12-31") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldBe Json.encodeToString(rollouts)
            }
        }
    }

    "should return error if query parameters are missing" {
        val databaseMock = mockk<Database>()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/report") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
                response.content shouldBe """{"error":"Missing query parameters"}"""
            }
        }
    }
})

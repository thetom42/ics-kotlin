package de.snuffel.rolloutapi.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*

class HealthTest : StringSpec({
    "should return health status" {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/health") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldBe """{"status":"healthy"}"""
            }
        }
    }
})

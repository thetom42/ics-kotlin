package de.snuffel.rolloutapi.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import de.snuffel.rolloutapi.db.Database
import de.snuffel.rolloutapi.models.Rollout
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class PersistTest : StringSpec({
    "should persist new rollout objects" {
        val databaseMock = mockk<Database>()
        val rollout1 = Rollout("1", mapOf("key" to "value1"))
        val rollout2 = Rollout("2", mapOf("key" to "value2"))
        val rollouts = listOf(rollout1, rollout2)

        every { databaseMock.getRolloutById("1") } returns null
        every { databaseMock.getRolloutById("2") } returns null
        every { databaseMock.insertRollout(any(), any()) } returns Unit

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/persist") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(rollouts))
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldBe """{"status":"success"}"""
            }
        }

        verify(exactly = 1) { databaseMock.insertRollout("1", any()) }
        verify(exactly = 1) { databaseMock.insertRollout("2", any()) }
    }

    "should not persist existing rollout objects" {
        val databaseMock = mockk<Database>()
        val rollout1 = Rollout("1", mapOf("key" to "value1"))
        val rollout2 = Rollout("2", mapOf("key" to "value2"))
        val rollouts = listOf(rollout1, rollout2)

        every { databaseMock.getRolloutById("1") } returns byteArrayOf()
        every { databaseMock.getRolloutById("2") } returns byteArrayOf()
        every { databaseMock.insertRollout(any(), any()) } returns Unit

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/persist") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(rollouts))
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldBe """{"status":"success"}"""
            }
        }

        verify(exactly = 0) { databaseMock.insertRollout("1", any()) }
        verify(exactly = 0) { databaseMock.insertRollout("2", any()) }
    }
})

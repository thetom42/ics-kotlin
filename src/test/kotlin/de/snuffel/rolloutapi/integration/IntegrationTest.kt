package de.snuffel.rolloutapi.integration

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import de.snuffel.rolloutapi.MainKt
import de.snuffel.rolloutapi.db.Database
import de.snuffel.rolloutapi.models.Rollout
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class IntegrationTest {

    @BeforeTest
    fun setup() {
        Database.init()
    }

    @Test
    fun testPersistEndpoint() {
        withTestApplication({ MainKt.main() }) {
            val rollout = Rollout(id = "1", data = mapOf("key" to "value"))
            val call = handleRequest(HttpMethod.Post, "/persist") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(listOf(rollout)))
            }

            assertEquals(HttpStatusCode.OK, call.response.status())
            assertEquals("""{"status":"success"}""", call.response.content)

            val storedRollout = Database.getRolloutById("1")
            assertNotNull(storedRollout)
            val storedRolloutObject = Json.decodeFromString<Rollout>(storedRollout)
            assertEquals(rollout, storedRolloutObject)
        }
    }

    @Test
    fun testReportEndpoint() {
        withTestApplication({ MainKt.main() }) {
            val rollout1 = Rollout(id = "1", data = mapOf("key" to "value1"))
            val rollout2 = Rollout(id = "2", data = mapOf("key" to "value2"))
            Database.insertRollout(rollout1.id, Json.encodeToString(rollout1))
            Database.insertRollout(rollout2.id, Json.encodeToString(rollout2))

            val call = handleRequest(HttpMethod.Get, "/report?start_date=2021-01-01&end_date=2021-12-31")

            assertEquals(HttpStatusCode.OK, call.response.status())
            val rollouts = Json.decodeFromString<List<Rollout>>(call.response.content!!)
            assertEquals(2, rollouts.size)
            assertTrue(rollouts.contains(rollout1))
            assertTrue(rollouts.contains(rollout2))
        }
    }

    @Test
    fun testHealthEndpoint() {
        withTestApplication({ MainKt.main() }) {
            val call = handleRequest(HttpMethod.Get, "/health")

            assertEquals(HttpStatusCode.OK, call.response.status())
            assertEquals("""{"status":"healthy"}""", call.response.content)
        }
    }
}

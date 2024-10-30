package de.snuffel.rolloutapi.e2e

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EndToEndTest : StringSpec({

    companion object {
        private lateinit var environment: DockerComposeContainer<*>

        @BeforeAll
        @JvmStatic
        fun setup() {
            environment = DockerComposeContainer<Nothing>(File("docker-compose.yml"))
                .withExposedService("rollout-api_1", 8080, Wait.forListeningPort())
                .withExposedService("db_1", 3306, Wait.forListeningPort())
                .withLocalCompose(true)
            environment.start()

            RestAssured.baseURI = "http://localhost"
            RestAssured.port = environment.getServicePort("rollout-api_1", 8080)
        }
    }

    "Persist endpoint should store rollout objects in the database" {
        val rollouts = listOf(
            mapOf("id" to "1", "data" to mapOf("key" to "value1")),
            mapOf("id" to "2", "data" to mapOf("key" to "value2"))
        )

        val response: Response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(rollouts)
            .post("/persist")

        response.statusCode shouldBe 200
        response.jsonPath().getString("status") shouldBe "success"
    }

    "Report endpoint should return rollout objects within the date range" {
        val response: Response = RestAssured.given()
            .queryParam("start_date", "2022-01-01")
            .queryParam("end_date", "2022-12-31")
            .get("/report")

        response.statusCode shouldBe 200
        val rollouts = response.jsonPath().getList<Map<String, Any>>("")
        rollouts.size shouldBe 2
    }

    "Health endpoint should return the health status of the application" {
        val response: Response = RestAssured.given()
            .get("/health")

        response.statusCode shouldBe 200
        response.jsonPath().getString("status") shouldBe "healthy"
    }
})

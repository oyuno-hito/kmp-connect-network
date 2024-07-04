package infra.http

import domain.HttpCondition
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json

class Ktor(private val baseUrl: String = "https://httpbin.org") {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun get(): HttpCondition {
        val response = client.get("$baseUrl/get")

        return HttpCondition(
            response.status.value,
            response.headers["Date"] ?: "",
        )

    }


    suspend fun post(): HttpCondition {
        val response = client.post("$baseUrl/post")

        return HttpCondition(
            response.status.value,
            response.headers["Date"] ?: "",
        )
    }
}
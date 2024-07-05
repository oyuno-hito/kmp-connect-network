package infra.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class Ktor(val baseUrl: String = "https://httpbin.org") {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
}
package infra.repository

import domain.HttpCondition
import infra.client.Ktor
import io.ktor.client.request.get
import io.ktor.client.request.post

class HttpRepository(private val ktor: Ktor) {
    suspend fun get(): HttpCondition {
        val response = ktor.client.get("${ktor.baseUrl}/get")

        return HttpCondition(
            response.status.value,
            response.headers["Date"] ?: "",
        )

    }


    suspend fun post(): HttpCondition {
        val response = ktor.client.post("${ktor.baseUrl}/post")

        return HttpCondition(
            response.status.value,
            response.headers["Date"] ?: "",
        )
    }
}
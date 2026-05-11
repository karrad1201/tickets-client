package com.karrad.ticketsclient.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class PushTokenRequest(val token: String, val platform: String)

class PushApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : PushService {

    override suspend fun registerToken(token: String, platform: String) {
        httpClient.post("$baseUrl/api/v1/push/register") {
            contentType(ContentType.Application.Json)
            setBody(PushTokenRequest(token, platform))
        }
    }

    override suspend fun unregisterToken(token: String) {
        httpClient.delete("$baseUrl/api/v1/push/token") {
            contentType(ContentType.Application.Json)
            setBody(PushTokenRequest(token, platform = ""))
        }
    }
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class AddFavoriteRequest(val eventId: String)

class FavoriteApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : FavoriteService {

    override suspend fun add(eventId: String) {
        httpClient.post("$baseUrl/api/v1/favorites") {
            contentType(ContentType.Application.Json)
            setBody(AddFavoriteRequest(eventId))
        }
    }

    override suspend fun remove(eventId: String) {
        httpClient.delete("$baseUrl/api/v1/favorites/$eventId")
    }

    override suspend fun list(): List<EventDto> =
        httpClient.get("$baseUrl/api/v1/favorites").body()
}

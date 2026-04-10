package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class DiscoveryApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : DiscoveryService {
    override suspend fun getDiscoveryFeed(
        city: String,
        authToken: String?,
        page: Int,
        size: Int,
        date: String?
    ): DiscoveryFeedResponseDto {
        return httpClient.get("$baseUrl/api/discovery") {
            parameter("city", city)
            parameter("page", page)
            parameter("size", size)
            date?.let { parameter("date", it) }
            authToken?.let { header("Authorization", "Bearer $it") }
        }.body()
    }
}

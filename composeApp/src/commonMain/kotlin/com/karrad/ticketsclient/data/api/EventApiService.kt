package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateEventRequest
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.SeatMapDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EventApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : EventService {

    override suspend fun getEvent(eventId: String): EventDto =
        httpClient.get("$baseUrl/api/v1/events/$eventId").body()

    override suspend fun search(
        query: String,
        city: String,
        page: Int,
        dateFrom: String?,
        dateTo: String?,
        categoryIds: List<String>
    ): List<EventDto> =
        httpClient.get("$baseUrl/api/v1/events/search") {
            parameter("q", query)
            parameter("city", city)
            parameter("page", page)
            if (dateFrom != null) parameter("dateFrom", dateFrom)
            if (dateTo != null) parameter("dateTo", dateTo)
            categoryIds.forEach { parameter("categoryId", it) }
        }.body()

    override suspend fun getTicketTypes(eventId: String): List<TicketTypeDto> =
        httpClient.get("$baseUrl/api/v1/inventory/$eventId/ticket-types").body()

    override suspend fun getSeatMap(eventId: String): SeatMapDto =
        httpClient.get("$baseUrl/api/v1/inventory/$eventId/seat-map").body()

    override suspend fun createEvent(request: CreateEventRequest): EventDto =
        httpClient.post("$baseUrl/api/v1/events") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}

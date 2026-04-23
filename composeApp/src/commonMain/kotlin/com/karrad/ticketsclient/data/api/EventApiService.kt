package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.SeatMapDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

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
        dateTo: String?
    ): List<EventDto> =
        httpClient.get("$baseUrl/api/v1/events/search") {
            parameter("q", query)
            parameter("city", city)
            parameter("page", page)
            if (dateFrom != null) parameter("dateFrom", dateFrom)
            if (dateTo != null) parameter("dateTo", dateTo)
        }.body()

    override suspend fun getTicketTypes(eventId: String): List<TicketTypeDto> =
        httpClient.get("$baseUrl/api/v1/inventory/$eventId/ticket-types").body()

    override suspend fun getSeatMap(eventId: String): SeatMapDto =
        httpClient.get("$baseUrl/api/v1/inventory/$eventId/seat-map").body()
}

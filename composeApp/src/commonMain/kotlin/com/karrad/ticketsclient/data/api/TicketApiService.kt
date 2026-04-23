package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.TicketDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get

class TicketApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : TicketService {

    override suspend fun getMyTickets(authToken: String): List<TicketDto> =
        httpClient.get("$baseUrl/api/v1/tickets/me") {
            bearerAuth(authToken)
        }.body()
}

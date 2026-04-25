package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.TicketValidationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class ScannerApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : ScannerService {

    override suspend fun getMyOrgEvents(): List<OrgEventItem> =
        httpClient.get("$baseUrl/api/v1/my/organization/events").body()

    override suspend fun validateTicket(eventId: String, ticketId: String): TicketValidationResponse =
        httpClient.post("$baseUrl/api/v1/events/$eventId/tickets/$ticketId/validate").body()
}

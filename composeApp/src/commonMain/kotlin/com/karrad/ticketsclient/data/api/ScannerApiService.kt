package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.TicketValidationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post

class ScannerApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : ScannerService {

    override suspend fun getMyOrgEvents(authToken: String?): List<OrgEventItem> =
        httpClient.get("$baseUrl/api/v1/my/organization/events") {
            authToken?.let { header("Authorization", "Bearer $it") }
        }.body()

    override suspend fun validateTicket(
        eventId: String,
        ticketId: String,
        authToken: String?
    ): TicketValidationResponse =
        httpClient.post("$baseUrl/api/v1/events/$eventId/tickets/$ticketId/validate") {
            authToken?.let { header("Authorization", "Bearer $it") }
        }.body()
}

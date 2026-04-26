package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.RequestVenueAccessRequest
import com.karrad.ticketsclient.data.api.dto.VenueAccessGrantDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class VenueAccessGrantApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : VenueAccessGrantService {

    override suspend fun getIncomingRequests(): List<VenueAccessGrantDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/incoming-access-requests").body()

    override suspend fun getOutgoingRequests(): List<VenueAccessGrantDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/outgoing-access-requests").body()

    override suspend fun requestAccess(venueId: String, requestingOrgId: String): VenueAccessGrantDto =
        httpClient.post("$baseUrl/api/v1/venues/$venueId/access-requests") {
            contentType(ContentType.Application.Json)
            setBody(RequestVenueAccessRequest(requestingOrgId = requestingOrgId))
        }.body()

    override suspend fun approve(venueId: String, grantId: String): VenueAccessGrantDto =
        httpClient.post("$baseUrl/api/v1/venues/$venueId/access-requests/$grantId/approve").body()

    override suspend fun reject(venueId: String, grantId: String): VenueAccessGrantDto =
        httpClient.post("$baseUrl/api/v1/venues/$venueId/access-requests/$grantId/reject").body()
}

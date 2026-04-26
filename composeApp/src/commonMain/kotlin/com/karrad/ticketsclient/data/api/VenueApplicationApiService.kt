package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateVenueApplicationRequest
import com.karrad.ticketsclient.data.api.dto.VenueApplicationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class VenueApplicationApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : VenueApplicationService {

    override suspend fun submit(request: CreateVenueApplicationRequest): VenueApplicationDto =
        httpClient.post("$baseUrl/api/v1/my/organization/venue-applications") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun listMine(): List<VenueApplicationDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/venue-applications").body()
}

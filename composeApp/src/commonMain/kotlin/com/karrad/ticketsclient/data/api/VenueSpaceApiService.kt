package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateSpacePriceProfileRequest
import com.karrad.ticketsclient.data.api.dto.CreateVenueSpaceRequest
import com.karrad.ticketsclient.data.api.dto.SpacePriceProfileDto
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class VenueSpaceApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : VenueSpaceService {

    override suspend fun list(venueId: String): List<VenueSpaceDto> =
        httpClient.get("$baseUrl/api/v1/venues/$venueId/spaces").body()

    override suspend fun add(venueId: String, request: CreateVenueSpaceRequest): VenueSpaceDto =
        httpClient.post("$baseUrl/api/v1/venues/$venueId/spaces") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun listPriceProfiles(spaceId: String): List<SpacePriceProfileDto> =
        httpClient.get("$baseUrl/api/v1/venue-spaces/$spaceId/price-profiles").body()

    override suspend fun createPriceProfile(spaceId: String, request: CreateSpacePriceProfileRequest): SpacePriceProfileDto =
        httpClient.post("$baseUrl/api/v1/venue-spaces/$spaceId/price-profiles") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deletePriceProfile(spaceId: String, profileId: String) {
        httpClient.delete("$baseUrl/api/v1/venue-spaces/$spaceId/price-profiles/$profileId")
    }
}

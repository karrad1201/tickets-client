package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.VenueAccessGrantDto

class FakeVenueAccessGrantService : VenueAccessGrantService {

    private val incoming = mutableListOf(
        VenueAccessGrantDto(
            id = "grant-1",
            venueId = "venue-1",
            requestingOrgId = "org-2",
            status = "PENDING",
            createdAt = "2026-04-24T10:00:00Z"
        )
    )

    private val outgoing = mutableListOf(
        VenueAccessGrantDto(
            id = "grant-2",
            venueId = "venue-ext-1",
            requestingOrgId = "org-1",
            status = "PENDING",
            createdAt = "2026-04-23T09:00:00Z"
        )
    )

    override suspend fun getIncomingRequests(authToken: String): List<VenueAccessGrantDto> = incoming.toList()

    override suspend fun getOutgoingRequests(authToken: String): List<VenueAccessGrantDto> = outgoing.toList()

    override suspend fun requestAccess(authToken: String, venueId: String, requestingOrgId: String): VenueAccessGrantDto {
        val grant = VenueAccessGrantDto(
            id = "grant-new-${outgoing.size}",
            venueId = venueId,
            requestingOrgId = requestingOrgId,
            status = "PENDING",
            createdAt = "2026-04-25T10:00:00Z"
        )
        outgoing.add(grant)
        return grant
    }

    override suspend fun approve(authToken: String, venueId: String, grantId: String): VenueAccessGrantDto {
        val index = incoming.indexOfFirst { it.id == grantId }
        val updated = incoming[index].copy(status = "APPROVED")
        incoming[index] = updated
        return updated
    }

    override suspend fun reject(authToken: String, venueId: String, grantId: String): VenueAccessGrantDto {
        val index = incoming.indexOfFirst { it.id == grantId }
        val updated = incoming[index].copy(status = "REJECTED")
        incoming[index] = updated
        return updated
    }
}

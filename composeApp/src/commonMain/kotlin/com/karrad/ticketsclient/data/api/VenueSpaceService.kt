package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateVenueSpaceRequest
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto

interface VenueSpaceService {
    suspend fun list(venueId: String): List<VenueSpaceDto>
    suspend fun add(venueId: String, request: CreateVenueSpaceRequest): VenueSpaceDto
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateVenueSpaceRequest
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto
import java.util.UUID

class FakeVenueSpaceService : VenueSpaceService {

    private val storage = mutableMapOf<String, MutableList<VenueSpaceDto>>()

    override suspend fun list(venueId: String): List<VenueSpaceDto> =
        storage[venueId] ?: emptyList()

    override suspend fun add(venueId: String, request: CreateVenueSpaceRequest): VenueSpaceDto {
        val space = VenueSpaceDto(
            id = UUID.randomUUID().toString(),
            label = request.label,
            type = request.type,
            capacity = request.capacity
        )
        storage.getOrPut(venueId) { mutableListOf() }.add(space)
        return space
    }
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateSpacePriceProfileRequest
import com.karrad.ticketsclient.data.api.dto.CreateVenueSpaceRequest
import com.karrad.ticketsclient.data.api.dto.SpacePriceProfileDto
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto
import java.util.UUID

class FakeVenueSpaceService : VenueSpaceService {

    private val storage = mutableMapOf<String, MutableList<VenueSpaceDto>>()
    private val profileStorage = mutableMapOf<String, MutableList<SpacePriceProfileDto>>()

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

    override suspend fun listPriceProfiles(spaceId: String): List<SpacePriceProfileDto> =
        profileStorage[spaceId] ?: emptyList()

    override suspend fun createPriceProfile(spaceId: String, request: CreateSpacePriceProfileRequest): SpacePriceProfileDto {
        val profile = SpacePriceProfileDto(
            id = UUID.randomUUID().toString(),
            venueSpaceId = spaceId,
            label = request.label,
            mode = request.mode,
            sectionPrices = request.sectionPrices,
            ticketTypes = request.ticketTypes
        )
        profileStorage.getOrPut(spaceId) { mutableListOf() }.add(profile)
        return profile
    }

    override suspend fun deletePriceProfile(spaceId: String, profileId: String) {
        profileStorage[spaceId]?.removeIf { it.id == profileId }
    }
}

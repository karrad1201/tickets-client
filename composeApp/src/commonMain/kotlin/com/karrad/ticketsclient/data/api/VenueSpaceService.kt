package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateSpacePriceProfileRequest
import com.karrad.ticketsclient.data.api.dto.CreateVenueSpaceRequest
import com.karrad.ticketsclient.data.api.dto.SpacePriceProfileDto
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto

interface VenueSpaceService {
    suspend fun list(venueId: String): List<VenueSpaceDto>
    suspend fun add(venueId: String, request: CreateVenueSpaceRequest): VenueSpaceDto
    suspend fun listPriceProfiles(spaceId: String): List<SpacePriceProfileDto>
    suspend fun createPriceProfile(spaceId: String, request: CreateSpacePriceProfileRequest): SpacePriceProfileDto
    suspend fun deletePriceProfile(spaceId: String, profileId: String)
}

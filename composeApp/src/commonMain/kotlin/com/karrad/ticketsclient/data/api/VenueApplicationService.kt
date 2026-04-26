package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateVenueApplicationRequest
import com.karrad.ticketsclient.data.api.dto.VenueApplicationDto

interface VenueApplicationService {
    suspend fun submit(request: CreateVenueApplicationRequest): VenueApplicationDto
    suspend fun listMine(): List<VenueApplicationDto>
    suspend fun uploadDocuments(applicationId: String, files: List<FileBytes>): VenueApplicationDto
}

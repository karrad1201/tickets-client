package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateVenueApplicationRequest
import com.karrad.ticketsclient.data.api.dto.VenueApplicationDto

class FakeVenueApplicationService : VenueApplicationService {

    private val applications = mutableListOf(
        VenueApplicationDto(
            id = "app-1",
            organizationId = "org-1",
            name = "Тестовая площадка",
            cityLabel = "Москва",
            subjectLabel = "Москва",
            address = "ул. Тестовая, 1",
            description = "Описание тестовой площадки",
            documentUrls = emptyList(),
            status = "PENDING",
            createdAt = "2026-04-25T10:00:00Z",
            venueId = null
        )
    )

    override suspend fun submit(request: CreateVenueApplicationRequest): VenueApplicationDto {
        val app = VenueApplicationDto(
            id = "app-${applications.size + 1}",
            organizationId = "org-1",
            name = request.name,
            cityLabel = request.cityLabel,
            subjectLabel = request.subjectLabel,
            address = request.address,
            description = request.description,
            documentUrls = emptyList(),
            status = "PENDING",
            createdAt = "2026-04-26T10:00:00Z",
            venueId = null
        )
        applications.add(app)
        return app
    }

    override suspend fun listMine(): List<VenueApplicationDto> = applications.toList()

    override suspend fun uploadDocuments(applicationId: String, files: List<FileBytes>): VenueApplicationDto {
        val index = applications.indexOfFirst { it.id == applicationId }
        val updated = applications[index].copy(
            documentUrls = applications[index].documentUrls + files.map { "fake://docs/${it.name}" }
        )
        applications[index] = updated
        return updated
    }
}

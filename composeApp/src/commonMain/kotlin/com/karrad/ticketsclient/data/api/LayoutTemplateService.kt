package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateLayoutTemplateRequest
import com.karrad.ticketsclient.data.api.dto.LayoutTemplateDto

interface LayoutTemplateService {
    suspend fun list(venueSpaceId: String): List<LayoutTemplateDto>
    suspend fun create(request: CreateLayoutTemplateRequest): LayoutTemplateDto
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateLayoutTemplateRequest
import com.karrad.ticketsclient.data.api.dto.LayoutTemplateDto
import com.karrad.ticketsclient.data.api.dto.RowDto
import com.karrad.ticketsclient.data.api.dto.SectionDto
import java.util.UUID

class FakeLayoutTemplateService : LayoutTemplateService {

    private val storage = mutableMapOf<String, MutableList<LayoutTemplateDto>>(
        "space-seated-1" to mutableListOf(
            LayoutTemplateDto(
                id = "lt-1",
                venueSpaceId = "space-seated-1",
                label = "Основная схема",
                sections = listOf(
                    SectionDto(
                        label = "ВИП",
                        key = "vip",
                        rows = listOf(
                            RowDto(label = "Ряд 1", key = "r1", startSeat = 1, endSeat = 10, price = 5000)
                        )
                    ),
                    SectionDto(
                        label = "Партер",
                        key = "parter",
                        rows = listOf(
                            RowDto(label = "Ряд 1", key = "r1", startSeat = 1, endSeat = 20, price = 2000),
                            RowDto(label = "Ряд 2", key = "r2", startSeat = 1, endSeat = 20, price = 1800)
                        )
                    )
                )
            )
        )
    )

    override suspend fun list(venueSpaceId: String): List<LayoutTemplateDto> =
        storage[venueSpaceId] ?: emptyList()

    override suspend fun create(request: CreateLayoutTemplateRequest): LayoutTemplateDto {
        val template = LayoutTemplateDto(
            id = UUID.randomUUID().toString(),
            venueSpaceId = request.venueSpaceId,
            label = request.label,
            sections = request.sections
        )
        storage.getOrPut(request.venueSpaceId) { mutableListOf() }.add(template)
        return template
    }
}

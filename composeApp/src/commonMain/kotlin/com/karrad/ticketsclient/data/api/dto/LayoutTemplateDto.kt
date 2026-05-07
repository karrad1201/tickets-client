package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LayoutTemplateDto(
    val id: String,
    val venueSpaceId: String,
    val label: String,
    val sections: List<SectionDto> = emptyList()
)

@Serializable
data class SectionDto(
    val label: String,
    val key: String,
    val rows: List<RowDto> = emptyList()
)

@Serializable
data class RowDto(
    val label: String,
    val key: String,
    val startSeat: Int,
    val endSeat: Int,
    val price: Int
) {
    val seatCount: Int get() = endSeat - startSeat + 1
}

@Serializable
data class CreateLayoutTemplateRequest(
    val venueSpaceId: String,
    val label: String,
    val sections: List<SectionDto>
)

@Serializable
data class CreateSeatedInventoryRequest(
    val layoutTemplateId: String
)

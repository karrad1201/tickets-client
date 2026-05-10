package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class VenueSpaceDto(
    val id: String,
    val label: String,
    val type: String = "ADMISSION",
    val capacity: Int = 0
)

@Serializable
data class CreateVenueSpaceRequest(
    val label: String,
    val type: String,
    val capacity: Int
)

@Serializable
data class SpacePriceProfileDto(
    val id: String,
    val venueSpaceId: String,
    val label: String,
    val mode: String,
    val sectionPrices: List<SectionPriceDto> = emptyList(),
    val ticketTypes: List<TicketTypeTemplateDto> = emptyList()
)

@Serializable
data class SectionPriceDto(val sectionKey: String, val price: Int)

@Serializable
data class TicketTypeTemplateDto(val label: String, val price: Int, val quota: Int)

@Serializable
data class CreateSpacePriceProfileRequest(
    val label: String,
    val mode: String,
    val sectionPrices: List<SectionPriceDto> = emptyList(),
    val ticketTypes: List<TicketTypeTemplateDto> = emptyList()
)

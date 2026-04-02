package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class DiscoveryFeedResponseDto(
    val forYou: List<EventDto>,
    val byCategory: List<CategoryEventsEntryDto>,
    val tomorrow: List<EventDto>,
    val dayAfterTomorrow: List<EventDto>
)

@Serializable
data class CategoryEventsEntryDto(
    val category: CategoryDto,
    val events: List<EventDto>
)

@Serializable
data class EventDto(
    val id: String,
    val label: String,
    val description: String,
    val venueId: String,
    val categoryId: String,
    val time: String,
    val imageUrl: String? = null,
    val minPrice: Int? = null,
    val ageRating: String? = null,   // TODO backend #25: "0+", "6+", "12+", "18+"
    val organizationId: String? = null,
    val salesClosedAt: String? = null
)

@Serializable
data class CategoryDto(
    val id: String,
    val code: String,
    val label: String
)

package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SeatMapDto(
    val sections: List<SeatSectionDto>
)

@Serializable
data class SeatSectionDto(
    val key: String,
    val label: String,
    val rows: List<SeatRowDto>
)

@Serializable
data class SeatRowDto(
    val key: String,
    val label: String,
    val seats: List<SeatItemDto>
)

@Serializable
data class SeatItemDto(
    val key: String,
    val price: Int,
    val available: Boolean
)

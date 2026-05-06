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

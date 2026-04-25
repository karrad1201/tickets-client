package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class VenueAccessGrantDto(
    val id: String,
    val venueId: String,
    val requestingOrgId: String,
    val status: String,
    val createdAt: String,
    val decidedAt: String? = null
)

@Serializable
data class RequestVenueAccessRequest(
    val requestingOrgId: String
)

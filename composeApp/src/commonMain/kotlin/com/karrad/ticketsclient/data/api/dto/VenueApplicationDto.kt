package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class VenueApplicationDto(
    val id: String,
    val organizationId: String,
    val name: String,
    val cityLabel: String,
    val subjectLabel: String,
    val address: String,
    val description: String? = null,
    val documentUrls: List<String> = emptyList(),
    val status: String,
    val createdAt: String,
    val venueId: String? = null
)

@Serializable
data class CreateVenueApplicationRequest(
    val name: String,
    val cityLabel: String,
    val subjectLabel: String,
    val address: String,
    val description: String? = null
)

package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrgEventItem(
    val id: String,
    val label: String,
    val time: String,
    val venueLabel: String? = null,
    val venueSpaceId: String? = null,
    val hasInventory: Boolean = false,
    val sold: Int = 0,
    val capacity: Int = 0
)

@Serializable
data class TicketValidationResponse(
    val status: String,
    val holderName: String? = null,
    @kotlinx.serialization.SerialName("seatInfo") val seat: String? = null,
    val usedAt: String? = null,
    val ticketEventLabel: String? = null,
    @kotlinx.serialization.SerialName("eventLabel") val scannedEventLabel: String? = null
)

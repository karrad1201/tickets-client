package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrgEventItem(
    val id: String,
    val label: String,
    val time: String
)

@Serializable
data class TicketValidationResponse(
    val status: String,
    val holderName: String? = null,
    val seat: String? = null,
    val usedAt: String? = null,
    val ticketEventLabel: String? = null,
    val scannedEventLabel: String? = null
)

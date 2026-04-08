package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketDto(
    val id: String,
    val eventId: String,
    val eventLabel: String,
    val seat: String? = null,
    val price: Int,
    val usedAt: String? = null,
    val venueName: String? = null,
    val eventTime: String? = null
)

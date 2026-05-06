package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketTypeDto(
    val id: String,
    val label: String,
    val price: Int,
    val quota: Int,
    val available: Int
)

@Serializable
data class CreateTicketTypeRequest(
    val label: String,
    val price: Int,
    val quota: Int
)

@Serializable
data class CreateGeneralAdmissionInventoryRequest(
    val ticketTypes: List<CreateTicketTypeRequest>
)

package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val eventId: String,
    val status: String, // PENDING, CONFIRMED, CANCELLED
    val totalPrice: Int,
    val ticketId: String? = null
)

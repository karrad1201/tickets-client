package com.karrad.ticketsclient.domain.model

import kotlinx.datetime.Instant

data class Ticket(
    val id: String,
    val orderId: String,
    val eventId: String,
    val price: Int,
    val issuedAt: Instant,
    val seatKey: SeatKey? = null,
    val ticketTypeId: String? = null
)

package com.karrad.ticketsclient.domain.model

import kotlinx.datetime.Instant

data class Order(
    val id: String,
    val eventId: String,
    val amount: Int,
    val status: OrderStatus,
    val paymentUrl: String,
    val expiresAt: Instant,
    val createdAt: Instant,
    val paidAt: Instant? = null
)

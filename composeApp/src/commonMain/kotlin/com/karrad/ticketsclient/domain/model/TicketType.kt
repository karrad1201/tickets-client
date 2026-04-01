package com.karrad.ticketsclient.domain.model

data class TicketType(
    val id: String,
    val label: String,
    val price: Int,
    val quota: Int? = null
)

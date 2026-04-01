package com.karrad.ticketsclient.domain.model

data class AdmissionOrderRequest(
    val ticketTypeId: String,
    val quantity: Int
)

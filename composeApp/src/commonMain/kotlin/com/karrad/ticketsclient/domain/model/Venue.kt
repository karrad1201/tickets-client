package com.karrad.ticketsclient.domain.model

data class Venue(
    val id: String,
    val label: String,
    val address: String,
    val city: City
)

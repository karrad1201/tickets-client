package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: String,
    val name: String,
    val region: String? = null
)

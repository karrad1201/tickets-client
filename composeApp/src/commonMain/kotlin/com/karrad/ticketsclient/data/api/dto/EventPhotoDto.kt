package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventPhotoDto(
    val id: String,
    val eventId: String,
    val url: String,
    val sortOrder: Int = 0
)

@Serializable
data class AttendeeDto(
    val userId: String,
    val name: String,
    val maskedPhone: String? = null
)

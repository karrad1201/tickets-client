package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    val id: String,
    val label: String,
    val subject: SubjectDto? = null
) {
    val name: String get() = label
    val region: String? get() = subject?.label
}

@Serializable
data class SubjectDto(val id: String, val label: String)

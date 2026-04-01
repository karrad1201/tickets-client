package com.karrad.ticketsclient.domain.model

import kotlinx.datetime.LocalDate

data class EventSearchFilter(
    val query: String = "",
    val categoryIds: List<String> = emptyList(),
    val city: City? = null,
    val date: LocalDate? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null
)

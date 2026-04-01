package com.karrad.ticketsclient.domain.model

import kotlinx.datetime.Instant

data class Event(
    val id: String,
    val label: String,
    val description: String,
    val venue: Venue,
    val category: Category,
    val time: Instant,
    val inventoryMode: InventoryMode,
    val minPrice: Int?,
    val imageUrl: String? = null,
    val organizationId: String? = null,
    val salesClosedAt: Instant? = null
) {
    fun isSalesClosed(now: Instant): Boolean =
        salesClosedAt != null || time <= now
}

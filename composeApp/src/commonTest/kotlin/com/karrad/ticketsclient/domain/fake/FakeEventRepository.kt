package com.karrad.ticketsclient.domain.fake

import com.karrad.ticketsclient.domain.model.DiscoveryFeed
import com.karrad.ticketsclient.domain.model.Event
import com.karrad.ticketsclient.domain.model.EventSearchFilter
import com.karrad.ticketsclient.domain.repository.EventRepository

class FakeEventRepository(
    private val events: MutableList<Event> = mutableListOf(),
    private val feed: DiscoveryFeed = DiscoveryFeed(
        forYou = emptyList(),
        byCategory = emptyMap(),
        tomorrow = emptyList(),
        dayAfterTomorrow = emptyList()
    )
) : EventRepository {

    override suspend fun getDiscoveryFeed(): DiscoveryFeed = feed

    override suspend fun getById(id: String): Event =
        events.first { it.id == id }

    override suspend fun search(filter: EventSearchFilter): List<Event> =
        events.filter { event ->
            (filter.query.isBlank() || event.label.contains(filter.query, ignoreCase = true)) &&
            (filter.categoryIds.isEmpty() || event.category.id in filter.categoryIds)
        }
}

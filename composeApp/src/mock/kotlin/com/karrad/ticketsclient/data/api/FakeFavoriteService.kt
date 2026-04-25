package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto

class FakeFavoriteService : FavoriteService {
    private val added = mutableSetOf<String>()

    override suspend fun add(eventId: String) {
        added.add(eventId)
    }

    override suspend fun remove(eventId: String) {
        added.remove(eventId)
    }

    override suspend fun list(): List<EventDto> {
        if (added.isEmpty()) return emptyList()
        val feed = FakeDiscoveryApiService.FEED
        val allEvents = (feed.forYou + feed.tomorrow + feed.dayAfterTomorrow +
            feed.byCategory.flatMap { it.events }).distinctBy { it.id }
        return allEvents.filter { it.id in added }
    }
}

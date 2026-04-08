package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto

/**
 * Мок-реализация для разработки без бекенда.
 * Использует те же события, что и FakeDiscoveryApiService.
 */
class FakeEventService : EventService {

    private val allEvents: List<EventDto> by lazy {
        val feed = FakeDiscoveryApiService.FEED
        (feed.forYou + feed.tomorrow + feed.dayAfterTomorrow +
            feed.byCategory.flatMap { it.events }).distinctBy { it.id }
    }

    override suspend fun getEvent(eventId: String): EventDto =
        allEvents.find { it.id == eventId } ?: error("Event not found: $eventId")

    override suspend fun search(query: String, city: String, page: Int): List<EventDto> =
        if (query.length < 2) emptyList()
        else allEvents.filter {
            it.label.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }
}

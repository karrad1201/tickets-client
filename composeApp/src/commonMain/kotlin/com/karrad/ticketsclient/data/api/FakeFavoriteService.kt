package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto

class FakeFavoriteService : FavoriteService {
    private val added = mutableSetOf<String>()

    override suspend fun add(eventId: String, token: String) {
        added.add(eventId)
    }

    override suspend fun remove(eventId: String, token: String) {
        added.remove(eventId)
    }

    override suspend fun list(token: String): List<EventDto> = emptyList()
}

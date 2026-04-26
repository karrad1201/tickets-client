package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto

interface FavoriteService {
    suspend fun add(eventId: String)
    suspend fun remove(eventId: String)
    suspend fun list(): List<EventDto>
}

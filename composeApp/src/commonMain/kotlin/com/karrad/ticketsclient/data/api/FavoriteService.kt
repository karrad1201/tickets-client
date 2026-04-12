package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto

interface FavoriteService {
    suspend fun add(eventId: String, token: String)
    suspend fun remove(eventId: String, token: String)
    suspend fun list(token: String): List<EventDto>
}

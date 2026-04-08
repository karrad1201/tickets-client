package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto

interface EventService {
    suspend fun getEvent(eventId: String): EventDto
    suspend fun search(query: String, city: String, page: Int = 0): List<EventDto>
}

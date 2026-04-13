package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.SeatMapDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeDto

interface EventService {
    suspend fun getEvent(eventId: String): EventDto
    suspend fun search(
        query: String,
        city: String,
        page: Int = 0,
        dateFrom: String? = null,
        dateTo: String? = null
    ): List<EventDto>
    suspend fun getTicketTypes(eventId: String): List<TicketTypeDto>
    suspend fun getSeatMap(eventId: String): SeatMapDto
}

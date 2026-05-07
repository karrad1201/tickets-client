package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateEventRequest
import com.karrad.ticketsclient.data.api.dto.CreateGeneralAdmissionInventoryRequest
import com.karrad.ticketsclient.data.api.dto.CreateSeatedInventoryRequest
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.SeatMapDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeDto
import com.karrad.ticketsclient.data.api.dto.UpdateEventRequest

interface EventService {
    suspend fun getEvent(eventId: String): EventDto
    suspend fun search(
        query: String,
        city: String,
        page: Int = 0,
        dateFrom: String? = null,
        dateTo: String? = null,
        categoryIds: List<String> = emptyList()
    ): List<EventDto>
    suspend fun getTicketTypes(eventId: String): List<TicketTypeDto>
    suspend fun getSeatMap(eventId: String): SeatMapDto
    suspend fun createEvent(request: CreateEventRequest): EventDto
    suspend fun uploadCover(eventId: String, file: FileBytes)
    suspend fun updateEvent(eventId: String, request: UpdateEventRequest): EventDto
    suspend fun createGeneralAdmissionInventory(eventId: String, request: CreateGeneralAdmissionInventoryRequest)
    suspend fun createSeatedInventory(eventId: String, request: CreateSeatedInventoryRequest)
}

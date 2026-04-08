package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.TicketDto

interface TicketService {
    suspend fun getMyTickets(authToken: String): List<TicketDto>
}

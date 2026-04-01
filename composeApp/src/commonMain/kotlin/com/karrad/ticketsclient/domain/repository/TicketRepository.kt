package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.Ticket

interface TicketRepository {
    suspend fun getMyTickets(): List<Ticket>
    suspend fun getByOrderId(orderId: String): List<Ticket>
}

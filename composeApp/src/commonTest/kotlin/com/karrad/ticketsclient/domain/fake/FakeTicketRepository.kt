package com.karrad.ticketsclient.domain.fake

import com.karrad.ticketsclient.domain.model.Ticket
import com.karrad.ticketsclient.domain.repository.TicketRepository

class FakeTicketRepository(
    private val tickets: List<Ticket> = emptyList()
) : TicketRepository {

    override suspend fun getMyTickets(): List<Ticket> = tickets

    override suspend fun getByOrderId(orderId: String): List<Ticket> =
        tickets.filter { it.orderId == orderId }
}

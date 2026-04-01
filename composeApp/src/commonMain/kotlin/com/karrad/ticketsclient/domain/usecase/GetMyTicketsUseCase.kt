package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.Ticket
import com.karrad.ticketsclient.domain.repository.TicketRepository

class GetMyTicketsUseCase(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(): List<Ticket> =
        ticketRepository.getMyTickets()
}

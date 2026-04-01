package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.Event
import com.karrad.ticketsclient.domain.repository.EventRepository

class GetEventDetailUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String): Event =
        eventRepository.getById(eventId)
}

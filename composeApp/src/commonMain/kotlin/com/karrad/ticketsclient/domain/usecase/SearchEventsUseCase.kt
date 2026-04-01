package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.Event
import com.karrad.ticketsclient.domain.model.EventSearchFilter
import com.karrad.ticketsclient.domain.repository.EventRepository

class SearchEventsUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(filter: EventSearchFilter): List<Event> =
        eventRepository.search(filter)
}

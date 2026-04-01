package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.DiscoveryFeed
import com.karrad.ticketsclient.domain.repository.EventRepository

class GetDiscoveryFeedUseCase(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(): DiscoveryFeed =
        eventRepository.getDiscoveryFeed()
}

package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.DiscoveryFeed
import com.karrad.ticketsclient.domain.model.Event
import com.karrad.ticketsclient.domain.model.EventSearchFilter

interface EventRepository {
    suspend fun getDiscoveryFeed(): DiscoveryFeed
    suspend fun getById(id: String): Event
    suspend fun search(filter: EventSearchFilter): List<Event>
}

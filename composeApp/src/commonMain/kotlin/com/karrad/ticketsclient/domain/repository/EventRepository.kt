package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.DiscoveryFeed
import com.karrad.ticketsclient.domain.model.Event

interface EventRepository {
    suspend fun getDiscoveryFeed(): DiscoveryFeed
    suspend fun getById(id: String): Event
}

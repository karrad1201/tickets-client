package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto

interface DiscoveryService {
    suspend fun getDiscoveryFeed(
        city: String,
        authToken: String? = null,
        page: Int = 0,
        size: Int = 20
    ): DiscoveryFeedResponseDto
}

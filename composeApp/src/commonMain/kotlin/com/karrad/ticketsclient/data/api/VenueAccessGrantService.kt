package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.VenueAccessGrantDto

interface VenueAccessGrantService {
    /** Входящие запросы на аренду площадок организации (для OWNER). */
    suspend fun getIncomingRequests(authToken: String): List<VenueAccessGrantDto>

    /** Исходящие запросы на аренду чужих площадок (для OWNER/MANAGER). */
    suspend fun getOutgoingRequests(authToken: String): List<VenueAccessGrantDto>

    /** Отправить запрос на аренду площадки. */
    suspend fun requestAccess(authToken: String, venueId: String, requestingOrgId: String): VenueAccessGrantDto

    /** Одобрить входящий запрос. */
    suspend fun approve(authToken: String, venueId: String, grantId: String): VenueAccessGrantDto

    /** Отклонить входящий запрос. */
    suspend fun reject(authToken: String, venueId: String, grantId: String): VenueAccessGrantDto
}

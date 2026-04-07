package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.TicketValidationResponse

interface ScannerService {
    suspend fun getMyOrgEvents(authToken: String?): List<OrgEventItem>
    suspend fun validateTicket(eventId: String, ticketId: String, authToken: String?): TicketValidationResponse
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.TicketValidationResponse

interface ScannerService {
    suspend fun getMyOrgEvents(): List<OrgEventItem>
    suspend fun validateTicket(eventId: String, ticketId: String): TicketValidationResponse
}

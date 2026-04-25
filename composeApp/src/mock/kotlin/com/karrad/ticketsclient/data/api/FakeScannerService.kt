package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.TicketValidationResponse

class FakeScannerService : ScannerService {

    private val orgEvents = listOf(
        OrgEventItem("evt-001", "Лебединое озеро", "2026-04-05T15:00:00Z"),
        OrgEventItem("evt-021", "Вечеринка 90-х", "2026-04-03T18:00:00Z")
    )

    override suspend fun getMyOrgEvents(): List<OrgEventItem> = orgEvents

    override suspend fun validateTicket(eventId: String, ticketId: String): TicketValidationResponse =
        when (ticketId.trim().lowercase()) {
            "t-001" -> TicketValidationResponse(status = "VALID", holderName = "Иван Иванов", seat = "Партер, ряд 3, место 7")
            "t-002" -> TicketValidationResponse(status = "VALID", holderName = "Мария Смирнова", seat = "Партер, ряд 1, место 12")
            "t-003" -> TicketValidationResponse(status = "ALREADY_USED", holderName = "Пётр Петров", seat = "Ряд 5, место 9", usedAt = "2026-03-02T14:05:00Z")
            "t-999" -> TicketValidationResponse(
                status = "WRONG_EVENT",
                ticketEventLabel = "Другое мероприятие",
                scannedEventLabel = orgEvents.find { it.id == eventId }?.label ?: eventId
            )
            else -> TicketValidationResponse(status = "NOT_FOUND")
        }
}

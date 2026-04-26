package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.TicketDto

class FakeTicketService : TicketService {

    override suspend fun getMyTickets(): List<TicketDto> = listOf(
        TicketDto(id = "t-001", eventId = "e-001", eventLabel = "Лебединое озеро", seat = "Партер, ряд 3, место 7", price = 2000, usedAt = null, venueName = "Большой театр", eventTime = "5 апр 2026, 15:00"),
        TicketDto(id = "t-002", eventId = "e-002", eventLabel = "Вечеринка 90-х", seat = "Партер, ряд 1, место 12", price = 1200, usedAt = null, venueName = "Арена", eventTime = "3 апр 2026, 18:00"),
        TicketDto(id = "t-003", eventId = "e-003", eventLabel = "Дюна: Часть вторая", seat = "Ряд 5, место 9", price = 350, usedAt = "2026-03-02T14:00:00Z", venueName = "Кинотеатр Октябрь", eventTime = "2 мар 2026, 14:00"),
        TicketDto(id = "t-004", eventId = "e-004", eventLabel = "Вечер стендапа", seat = "Балкон, место 4", price = 900, usedAt = "2026-02-15T19:00:00Z", venueName = "Известия Hall", eventTime = "15 фев 2026, 19:00")
    )
}

package com.karrad.ticketsclient

import com.karrad.ticketsclient.data.api.dto.EventDto

/**
 * In-memory app session. Holds user-selected city, auth token and a short-lived
 * event reference used when navigating to EventDetailScreen.
 */
object AppSession {
    var city: String = "Москва"
    var authToken: String? = null

    /** Set before pushing EventDetailScreen; read inside that screen. */
    var currentEvent: EventDto? = null

    // Profile — хранится локально до появления реального API
    var userName: String = "Иван Иванов"
    var userPhone: String = "+7 (999) 123-45-67"
    var userCity: String = city
    var userInterests: List<String> = listOf("Театры", "Кино", "Концерты")

    // Мок-билеты для TicketsScreen
    val mockTickets: List<MockTicket> = listOf(
        MockTicket("t-001", "Лебединое озеро", "Большой театр", "5 апр 2026, 15:00", "Партер, ряд 3, место 7", 2000, TicketStatus.UPCOMING),
        MockTicket("t-002", "Вечеринка 90-х", "Арена", "3 апр 2026, 18:00", "Партер, ряд 1, место 12", 1200, TicketStatus.UPCOMING),
        MockTicket("t-003", "Дюна: Часть вторая", "Кинотеатр Октябрь", "2 мар 2026, 14:00", "Ряд 5, место 9", 350, TicketStatus.USED),
        MockTicket("t-004", "Вечер стендапа", "Известия Hall", "15 фев 2026, 19:00", "Балкон, место 4", 900, TicketStatus.USED)
    )
}

data class MockTicket(
    val id: String,
    val eventName: String,
    val venue: String,
    val datetime: String,
    val seat: String,
    val price: Int,
    val status: TicketStatus
)

enum class TicketStatus { UPCOMING, USED }

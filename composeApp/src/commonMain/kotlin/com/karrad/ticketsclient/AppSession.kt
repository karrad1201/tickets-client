package com.karrad.ticketsclient

import com.karrad.ticketsclient.data.api.dto.EventDto

/**
 * In-memory app session. Holds auth state and short-lived navigation data.
 * Не переживает рестарт приложения — токен хранится только в памяти.
 */
object AppSession {
    var authToken: String? = null
    var userId: String? = null

    var city: String = "Москва"

    /** Set before pushing EventDetailScreen; read inside that screen. */
    var currentEvent: EventDto? = null

    // Profile — заполняется при входе/регистрации
    var userName: String = ""
    var userPhone: String = ""
    var userCity: String = city
    var userInterests: List<String> = emptyList()

    // Кеш всех событий — заполняется FeedViewModel после загрузки, используется поиском
    var cachedEvents: List<EventDto> = emptyList()

    // Мок-билеты для TicketsScreen
    val mockTickets: List<MockTicket> = listOf(
        MockTicket("t-001", "Лебединое озеро", "Большой театр", "5 апр 2026, 15:00", "Партер, ряд 3, место 7", 2000, TicketStatus.UPCOMING),
        MockTicket("t-002", "Вечеринка 90-х", "Арена", "3 апр 2026, 18:00", "Партер, ряд 1, место 12", 1200, TicketStatus.UPCOMING),
        MockTicket("t-003", "Дюна: Часть вторая", "Кинотеатр Октябрь", "2 мар 2026, 14:00", "Ряд 5, место 9", 350, TicketStatus.USED),
        MockTicket("t-004", "Вечер стендапа", "Известия Hall", "15 фев 2026, 19:00", "Балкон, место 4", 900, TicketStatus.USED)
    )

    var userRole: String = "USER"

    fun login(token: String, userId: String, phone: String?, fullName: String, role: String) {
        this.authToken = token
        this.userId = userId
        this.userPhone = phone ?: ""
        this.userName = fullName
        this.userRole = role
    }

    fun logout() {
        authToken = null
        userId = null
        userName = ""
        userPhone = ""
        userRole = "USER"
        userInterests = emptyList()
        cachedEvents = emptyList()
        currentEvent = null
    }
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

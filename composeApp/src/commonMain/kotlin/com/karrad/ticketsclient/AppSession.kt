package com.karrad.ticketsclient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.karrad.ticketsclient.data.api.dto.EventDto

/**
 * In-memory app session. Holds auth state and short-lived navigation data.
 * Не переживает рестарт приложения — токен хранится только в памяти.
 */
object AppSession {
    var authToken: String? = null
    var userId: String? = null

    var city: String by mutableStateOf("Москва")

    // Profile — заполняется при входе/регистрации
    var userName: String = ""
    var userPhone: String = ""
    var userCity: String = city
    var userInterests: List<String> = emptyList()
    var userAvatarUrl: String? = null

    // Кеш всех событий — заполняется FeedViewModel после загрузки, используется поиском
    var cachedEvents: List<EventDto> = emptyList()

    // Кеш билетов для автономного режима — обновляется при каждом успешном запросе
    var cachedTickets: List<com.karrad.ticketsclient.data.api.dto.TicketDto> by mutableStateOf(emptyList())

    // true если последний запрос к API завершился ошибкой сети
    var isOffline: Boolean by mutableStateOf(false)

    // Локальное избранное (in-memory до реализации бэка, issue #22)
    private val _favorites = mutableSetOf<String>()
    fun isFavorite(eventId: String): Boolean = eventId in _favorites
    fun toggleFavorite(eventId: String, add: Boolean) {
        if (add) _favorites.add(eventId) else _favorites.remove(eventId)
    }

    var userRole: String = "USER"

    fun login(
        token: String,
        userId: String,
        phone: String?,
        fullName: String,
        role: String,
        avatarUrl: String? = null,
        interests: List<String> = emptyList()
    ) {
        this.authToken = token
        this.userId = userId
        this.userPhone = phone ?: ""
        this.userName = fullName
        this.userRole = role
        this.userAvatarUrl = avatarUrl
        this.userInterests = interests
    }

    fun logout() {
        authToken = null
        userId = null
        userName = ""
        userPhone = ""
        userRole = "USER"
        userInterests = emptyList()
        userAvatarUrl = null
        cachedEvents = emptyList()
    }
}

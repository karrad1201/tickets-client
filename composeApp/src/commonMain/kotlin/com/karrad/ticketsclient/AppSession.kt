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

    /** Set before pushing EventDetailScreen; read inside that screen. */
    var currentEvent: EventDto? = null

    // Profile — заполняется при входе/регистрации
    var userName: String = ""
    var userPhone: String = ""
    var userCity: String = city
    var userInterests: List<String> = emptyList()

    // Кеш всех событий — заполняется FeedViewModel после загрузки, используется поиском
    var cachedEvents: List<EventDto> = emptyList()

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

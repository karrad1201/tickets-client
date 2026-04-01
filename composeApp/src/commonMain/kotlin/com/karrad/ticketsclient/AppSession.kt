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
}

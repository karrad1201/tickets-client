package com.karrad.ticketsclient

/**
 * In-memory app session. Holds user-selected city and auth token for the current app lifecycle.
 * City is set during onboarding (CitySelectionScreen) and used by the feed and discovery APIs.
 */
object AppSession {
    var city: String = "Москва"
    var authToken: String? = null
}

package com.karrad.ticketsclient.data.store

data class SessionSnapshot(
    val token: String,
    val userId: String,
    val fullName: String,
    val phone: String,
    val role: String
)

/**
 * Персистентное хранилище токена аутентификации.
 * Android: SharedPreferences; iOS: NSUserDefaults.
 */
expect object TokenStore {
    fun save(snapshot: SessionSnapshot)
    fun load(): SessionSnapshot?
    fun clear()
}

package com.karrad.ticketsclient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.OrgMembershipDto
import com.karrad.ticketsclient.data.store.SessionSnapshot
import com.karrad.ticketsclient.data.store.TokenStore

/**
 * In-memory app session. Holds auth state and short-lived navigation data.
 * Токен сохраняется через TokenStore (SharedPreferences/NSUserDefaults).
 */
object AppSession {
    var authToken: String? = null
    var userId: String? = null

    var city: String by mutableStateOf("Москва")

    // Profile — заполняется при входе/регистрации
    var userName: String = ""
    var userPhone: String = ""
    var userInterests: List<String> = emptyList()
    var userAvatarUrl: String? = null

    // Кеш всех событий — заполняется FeedViewModel после загрузки, используется поиском
    var cachedEvents: List<EventDto> = emptyList()

    // Кеш билетов для автономного режима — обновляется при каждом успешном запросе
    var cachedTickets: List<com.karrad.ticketsclient.data.api.dto.TicketDto> by mutableStateOf(emptyList())

    // true если последний запрос к API завершился ошибкой сети
    var isOffline: Boolean by mutableStateOf(false)

    // Избранное (in-memory, синхронизируется с API в FavoritesScreen/FeedScreen)
    private val _favorites = mutableSetOf<String>()
    fun isFavorite(eventId: String): Boolean = eventId in _favorites
    fun toggleFavorite(eventId: String, add: Boolean) {
        if (add) _favorites.add(eventId) else _favorites.remove(eventId)
    }
    fun setFavorites(ids: Collection<String>) {
        _favorites.clear()
        _favorites.addAll(ids)
    }

    var userRole: String = "USER"

    // Версия приложения — устанавливается платформой при старте
    var appVersion: String = "1.0.0"

    // Членство в организации — загружается после входа в MainScreen
    var orgMembership: OrgMembershipDto? by mutableStateOf(null)

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
        TokenStore.save(SessionSnapshot(token, userId, fullName, phone ?: "", role))
    }

    /** Восстановить сессию из персистентного хранилища (вызывать при старте приложения). */
    fun restoreFromStore(): Boolean {
        val s = TokenStore.load() ?: return false
        authToken = s.token
        userId = s.userId
        userName = s.fullName
        userPhone = s.phone
        userRole = s.role
        return true
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
        cachedTickets = emptyList()
        orgMembership = null
        _favorites.clear()
        TokenStore.clear()
    }
}

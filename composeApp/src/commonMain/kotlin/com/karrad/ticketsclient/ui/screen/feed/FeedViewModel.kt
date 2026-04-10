package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

sealed interface FeedState {
    data object Loading : FeedState
    data class Success(val feed: DiscoveryFeedResponseDto) : FeedState
    data class Error(val message: String) : FeedState
}

class FeedViewModel(
    private val discoveryService: DiscoveryService
) : ViewModel() {

    private val _state = MutableStateFlow<FeedState>(FeedState.Loading)
    val state: StateFlow<FeedState> = _state.asStateFlow()

    private val _selectedDay = MutableStateFlow(0)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    private var currentDate: String? = null

    init {
        viewModelScope.launch {
            snapshotFlow { AppSession.city }
                .distinctUntilChanged()
                .collect {
                    currentDate = null
                    _selectedDay.value = 0
                    load()
                }
        }
    }

    fun selectDay(offset: Int) {
        _selectedDay.value = offset
        currentDate = if (offset == 0) {
            null
        } else {
            val tz = TimeZone.currentSystemDefault()
            val today = Clock.System.now().toLocalDateTime(tz).date
            today.plus(offset, DateTimeUnit.DAY).toString()
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = FeedState.Loading
            try {
                val feed = discoveryService.getDiscoveryFeed(
                    city = AppSession.city,
                    authToken = AppSession.authToken,
                    date = currentDate
                )
                AppSession.cachedEvents = (feed.forYou + feed.byCategory.flatMap { it.events }).distinctBy { it.id }
                AppSession.isOffline = false
                _state.value = FeedState.Success(feed)
            } catch (e: Exception) {
                AppSession.isOffline = true
                _state.value = FeedState.Error(e.message ?: "Ошибка загрузки ленты")
            }
        }
    }
}

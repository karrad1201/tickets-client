package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import com.karrad.ticketsclient.data.api.dto.EventDto
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
    data class Success(
        val feed: DiscoveryFeedResponseDto,
        val hasMore: Boolean = true
    ) : FeedState
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
    private var currentPage = 0
    private var accumulatedForYou = mutableListOf<EventDto>()
    private var isLoadingMore = false

    init {
        viewModelScope.launch {
            snapshotFlow { AppSession.city }
                .distinctUntilChanged()
                .collect {
                    currentDate = null
                    _selectedDay.value = 0
                    resetAndLoad()
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
        resetAndLoad()
    }

    fun load() = resetAndLoad()

    fun loadMore() {
        if (isLoadingMore) return
        val current = _state.value as? FeedState.Success ?: return
        if (!current.hasMore) return

        isLoadingMore = true
        viewModelScope.launch {
            try {
                currentPage++
                val next = discoveryService.getDiscoveryFeed(
                    city = AppSession.city,
                    authToken = AppSession.authToken,
                    page = currentPage,
                    date = currentDate
                )
                accumulatedForYou.addAll(next.forYou)
                val merged = current.feed.copy(forYou = accumulatedForYou.toList())
                AppSession.cachedEvents = (merged.forYou + merged.byCategory.flatMap { it.events }).distinctBy { it.id }
                _state.value = FeedState.Success(merged, hasMore = next.forYou.isNotEmpty())
            } catch (_: Exception) {
                // Keep current state, loadMore failed silently
            } finally {
                isLoadingMore = false
            }
        }
    }

    private fun resetAndLoad() {
        currentPage = 0
        accumulatedForYou = mutableListOf()
        viewModelScope.launch {
            _state.value = FeedState.Loading
            try {
                val feed = discoveryService.getDiscoveryFeed(
                    city = AppSession.city,
                    authToken = AppSession.authToken,
                    page = 0,
                    date = currentDate
                )
                accumulatedForYou = feed.forYou.toMutableList()
                AppSession.cachedEvents = (feed.forYou + feed.byCategory.flatMap { it.events }).distinctBy { it.id }
                AppSession.isOffline = false
                _state.value = FeedState.Success(feed, hasMore = feed.forYou.isNotEmpty())
            } catch (e: Exception) {
                AppSession.isOffline = true
                _state.value = FeedState.Error(e.message ?: "Ошибка загрузки ленты")
            }
        }
    }
}

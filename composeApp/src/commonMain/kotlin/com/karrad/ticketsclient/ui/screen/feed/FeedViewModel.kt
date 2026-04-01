package com.karrad.ticketsclient.ui.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.DiscoveryApiService
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FeedState {
    data object Loading : FeedState
    data class Success(val feed: DiscoveryFeedResponseDto) : FeedState
    data class Error(val message: String) : FeedState
}

class FeedViewModel(
    private val discoveryApiService: DiscoveryApiService
) : ViewModel() {

    private val _state = MutableStateFlow<FeedState>(FeedState.Loading)
    val state: StateFlow<FeedState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = FeedState.Loading
            try {
                val feed = discoveryApiService.getDiscoveryFeed(
                    city = AppSession.city,
                    authToken = AppSession.authToken
                )
                _state.value = FeedState.Success(feed)
            } catch (e: Exception) {
                _state.value = FeedState.Error(e.message ?: "Ошибка загрузки ленты")
            }
        }
    }
}

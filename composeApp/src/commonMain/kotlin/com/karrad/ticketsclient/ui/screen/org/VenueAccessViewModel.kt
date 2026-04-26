package com.karrad.ticketsclient.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.VenueAccessGrantService
import com.karrad.ticketsclient.data.api.dto.VenueAccessGrantDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VenueAccessState(
    val incoming: List<VenueAccessGrantDto> = emptyList(),
    val outgoing: List<VenueAccessGrantDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class VenueAccessViewModel(
    private val venueAccessGrantService: VenueAccessGrantService
) : ViewModel() {

    private val _state = MutableStateFlow(VenueAccessState())
    val state: StateFlow<VenueAccessState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                _state.value = VenueAccessState(
                    incoming = venueAccessGrantService.getIncomingRequests(),
                    outgoing = venueAccessGrantService.getOutgoingRequests()
                )
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = VenueAccessState(isLoading = false, error = e.message)
            }
        }
    }

    fun approve(venueId: String, grantId: String) {
        viewModelScope.launch {
            runCatching { venueAccessGrantService.approve(venueId, grantId) }
                .onFailure { CrashReporter.log(it) }
            load()
        }
    }

    fun reject(venueId: String, grantId: String) {
        viewModelScope.launch {
            runCatching { venueAccessGrantService.reject(venueId, grantId) }
                .onFailure { CrashReporter.log(it) }
            load()
        }
    }
}

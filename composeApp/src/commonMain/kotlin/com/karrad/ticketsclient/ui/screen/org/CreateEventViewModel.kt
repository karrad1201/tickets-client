package com.karrad.ticketsclient.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.EventService
import com.karrad.ticketsclient.data.api.FileBytes
import com.karrad.ticketsclient.data.api.OrgMemberService
import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CreateEventRequest
import com.karrad.ticketsclient.data.api.dto.VenueDto
import com.karrad.ticketsclient.data.api.GeoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateEventState(
    val venues: List<VenueDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val createdEventId: String? = null
)

class CreateEventViewModel(
    private val eventService: EventService,
    private val orgMemberService: OrgMemberService,
    private val geoService: GeoService
) : ViewModel() {

    private val _state = MutableStateFlow(CreateEventState())
    val state: StateFlow<CreateEventState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val venues = orgMemberService.listMyVenues()
                val categories = geoService.getCategories()
                _state.value = _state.value.copy(venues = venues, categories = categories, isLoading = false)
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun submit(
        label: String,
        description: String,
        venueId: String,
        categoryId: String,
        ageRating: String,
        isoTime: String,
        coverFile: FileBytes
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            try {
                val event = eventService.createEvent(
                    CreateEventRequest(
                        label = label,
                        description = description,
                        venueId = venueId,
                        categoryId = categoryId,
                        time = isoTime,
                        ageRating = ageRating
                    )
                )
                eventService.uploadCover(event.id, coverFile)
                _state.value = _state.value.copy(isSubmitting = false, createdEventId = event.id)
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(isSubmitting = false, error = e.message)
            }
        }
    }
}

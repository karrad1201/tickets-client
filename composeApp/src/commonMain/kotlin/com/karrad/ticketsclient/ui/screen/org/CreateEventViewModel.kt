package com.karrad.ticketsclient.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.EventService
import com.karrad.ticketsclient.data.api.FileBytes
import com.karrad.ticketsclient.data.api.OrgMemberService
import com.karrad.ticketsclient.data.api.VenueSpaceService
import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CreateEventRequest
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.SpacePriceProfileDto
import com.karrad.ticketsclient.data.api.dto.VenueDto
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto
import com.karrad.ticketsclient.data.api.GeoService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateEventState(
    val venues: List<VenueDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val spaces: List<VenueSpaceDto> = emptyList(),
    val spacesLoading: Boolean = false,
    val priceProfiles: List<SpacePriceProfileDto> = emptyList(),
    val priceProfilesLoading: Boolean = false,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val createdEvent: EventDto? = null
)

class CreateEventViewModel(
    private val eventService: EventService,
    private val orgMemberService: OrgMemberService,
    private val geoService: GeoService,
    private val venueSpaceService: VenueSpaceService
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

    fun onVenueSelected(venueId: String) {
        _state.value = _state.value.copy(spacesLoading = true, spaces = emptyList(), priceProfiles = emptyList())
        viewModelScope.launch {
            val spaces = runCatching { venueSpaceService.list(venueId) }.getOrDefault(emptyList())
            _state.value = _state.value.copy(spaces = spaces, spacesLoading = false)
        }
    }

    fun onSpaceSelected(spaceId: String) {
        _state.value = _state.value.copy(priceProfilesLoading = true, priceProfiles = emptyList())
        viewModelScope.launch {
            val profiles = runCatching { venueSpaceService.listPriceProfiles(spaceId) }.getOrDefault(emptyList())
            _state.value = _state.value.copy(priceProfiles = profiles, priceProfilesLoading = false)
        }
    }

    fun submit(
        label: String,
        description: String,
        venueId: String,
        categoryId: String,
        ageRating: String,
        sessionTimes: List<String>,
        coverFile: FileBytes,
        venueSpaceId: String?,
        priceProfileId: String?
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
                        time = sessionTimes.firstOrNull() ?: "",
                        ageRating = ageRating,
                        venueSpaceId = venueSpaceId,
                        hasSeatMap = priceProfileId != null,
                        priceProfileId = priceProfileId,
                        sessionTimes = if (sessionTimes.size > 1) sessionTimes else null
                    )
                )
                val coverError = runCatching { eventService.uploadCover(event.id, coverFile) }
                    .exceptionOrNull()?.also { CrashReporter.log(it) }
                // Re-fetch to get imageUrl populated after cover upload
                val freshEvent = if (coverError == null)
                    runCatching { eventService.getEvent(event.id) }.getOrDefault(event)
                else event
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    createdEvent = freshEvent,
                    error = if (coverError != null) "Мероприятие создано, но обложка не загружена" else null
                )
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(isSubmitting = false, error = e.message)
            }
        }
    }
}

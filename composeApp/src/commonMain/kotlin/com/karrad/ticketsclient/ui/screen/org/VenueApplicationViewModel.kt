package com.karrad.ticketsclient.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.FileBytes
import com.karrad.ticketsclient.data.api.VenueApplicationService
import com.karrad.ticketsclient.data.api.dto.CreateVenueApplicationRequest
import com.karrad.ticketsclient.data.api.dto.VenueApplicationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VenueApplicationState(
    val applications: List<VenueApplicationDto> = emptyList(),
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null,
    val uploadError: String? = null,
    val submitSuccess: Boolean = false
)

class VenueApplicationViewModel(
    private val venueApplicationService: VenueApplicationService
) : ViewModel() {

    private val _state = MutableStateFlow(VenueApplicationState())
    val state: StateFlow<VenueApplicationState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                _state.value = VenueApplicationState(
                    applications = venueApplicationService.listMine(),
                    isLoading = false
                )
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = VenueApplicationState(isLoading = false, error = e.message)
            }
        }
    }

    fun submit(request: CreateVenueApplicationRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)
            try {
                venueApplicationService.submit(request)
                _state.value = _state.value.copy(isSubmitting = false, submitSuccess = true)
                load()
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(isSubmitting = false, error = e.message)
            }
        }
    }

    fun uploadDocuments(applicationId: String, files: List<FileBytes>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true, uploadError = null)
            try {
                venueApplicationService.uploadDocuments(applicationId, files)
                _state.value = _state.value.copy(isUploading = false)
                load()
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(isUploading = false, uploadError = e.message)
            }
        }
    }

    fun clearSubmitSuccess() {
        _state.value = _state.value.copy(submitSuccess = false)
    }

    fun clearUploadError() {
        _state.value = _state.value.copy(uploadError = null)
    }
}

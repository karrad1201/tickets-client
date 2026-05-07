package com.karrad.ticketsclient.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.OrgMemberService
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.data.api.dto.VenueDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrgManagementState(
    val members: List<OrgMemberDto> = emptyList(),
    val venues: List<VenueDto> = emptyList(),
    val events: List<OrgEventItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val addError: String? = null,
    val accountCreated: Boolean = false,
    val leaveError: String? = null,
    val leftOrg: Boolean = false
)

class OrgManagementViewModel(
    private val orgMemberService: OrgMemberService
) : ViewModel() {

    private val _state = MutableStateFlow(OrgManagementState())
    val state: StateFlow<OrgManagementState> = _state.asStateFlow()

    init {
        loadMembers()
    }

    fun loadMembers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val members = orgMemberService.listMembers()
                val venues = try { orgMemberService.listMyVenues() } catch (_: Exception) { emptyList() }
                val events = try { orgMemberService.listMyEvents() } catch (_: Exception) { emptyList() }
                _state.value = OrgManagementState(members = members, venues = venues, events = events, isLoading = false)
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = OrgManagementState(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteMember(memberId: String) {
        viewModelScope.launch {
            runCatching { orgMemberService.deleteMember(memberId) }
                .onFailure { CrashReporter.log(it) }
            loadMembers()
        }
    }

    fun addMember(userId: String, role: String, venueId: String?) {
        viewModelScope.launch {
            runCatching { orgMemberService.addMember(userId = userId, role = role, venueId = venueId) }
                .onFailure { CrashReporter.log(it) }
            loadMembers()
        }
    }

    fun addMemberByPhone(phone: String, role: String, venueId: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(addError = null, accountCreated = false)
            try {
                val result = orgMemberService.addMemberByPhone(phone = phone, role = role, venueId = venueId)
                _state.value = _state.value.copy(accountCreated = result.accountCreated)
                loadMembers()
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(addError = e.message)
            }
        }
    }

    fun clearAddError() {
        _state.value = _state.value.copy(addError = null, accountCreated = false)
    }

    fun leaveOrganization() {
        viewModelScope.launch {
            _state.value = _state.value.copy(leaveError = null)
            try {
                orgMemberService.leaveOrganization()
                _state.value = _state.value.copy(leftOrg = true)
            } catch (e: Exception) {
                CrashReporter.log(e)
                _state.value = _state.value.copy(leaveError = e.message)
            }
        }
    }

    fun clearLeaveError() {
        _state.value = _state.value.copy(leaveError = null)
    }
}

package com.karrad.ticketsclient.ui.screen.org

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.OrgMemberService
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrgManagementState(
    val members: List<OrgMemberDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
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
                _state.value = OrgManagementState(members = orgMemberService.listMembers())
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
}

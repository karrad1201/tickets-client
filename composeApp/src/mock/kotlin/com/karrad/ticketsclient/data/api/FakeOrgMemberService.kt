package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AddMemberByPhoneResponse
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.data.api.dto.OrgMembershipDto
import com.karrad.ticketsclient.data.api.dto.VenueDto

class FakeOrgMemberService : OrgMemberService {

    private val members = mutableListOf(
        OrgMemberDto(id = "member-owner-1", organizationId = "org-1", userId = "user-owner", role = "OWNER"),
        OrgMemberDto(id = "member-mgr-1", organizationId = "org-1", userId = "user-mgr", role = "MANAGER"),
        OrgMemberDto(id = "member-staff-1", organizationId = "org-1", userId = "user-staff", role = "STAFF", venueId = "venue-1")
    )

    override suspend fun getMyMembership(): OrgMembershipDto =
        OrgMembershipDto(memberId = "member-owner-1", organizationId = "org-1", role = "OWNER")

    override suspend fun listMembers(): List<OrgMemberDto> = members.toList()

    override suspend fun listMyVenues(): List<VenueDto> = emptyList()

    override suspend fun addMember(userId: String, role: String, venueId: String?): OrgMemberDto {
        val member = OrgMemberDto(
            id = "member-new-${members.size}",
            organizationId = "org-1",
            userId = userId,
            role = role,
            venueId = venueId
        )
        members.add(member)
        return member
    }

    override suspend fun updateMember(memberId: String, role: String, venueId: String?): OrgMemberDto {
        val index = members.indexOfFirst { it.id == memberId }
        val updated = members[index].copy(role = role, venueId = venueId)
        members[index] = updated
        return updated
    }

    override suspend fun addMemberByPhone(phone: String, role: String, venueId: String?): AddMemberByPhoneResponse {
        val member = OrgMemberDto(
            id = "member-phone-${members.size}",
            organizationId = "org-1",
            userId = "user-phone-${members.size}",
            role = role,
            venueId = venueId
        )
        members.add(member)
        return AddMemberByPhoneResponse(member = member, accountCreated = false)
    }

    override suspend fun deleteMember(memberId: String) {
        members.removeAll { it.id == memberId }
    }

    override suspend fun leaveOrganization() {
        members.removeAll { it.userId == "user-owner" }
    }
}

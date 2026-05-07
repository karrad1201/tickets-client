package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AddMemberByPhoneRequest
import com.karrad.ticketsclient.data.api.dto.AddMemberByPhoneResponse
import com.karrad.ticketsclient.data.api.dto.AddMemberRequest
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.data.api.dto.OrgMembershipDto
import com.karrad.ticketsclient.data.api.dto.UpdateMemberRequest
import com.karrad.ticketsclient.data.api.dto.VenueDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class OrgMemberApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : OrgMemberService {

    override suspend fun getMyMembership(): OrgMembershipDto? {
        val response = httpClient.get("$baseUrl/api/v1/my/organization/membership")
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun listMembers(): List<OrgMemberDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/members").body()

    override suspend fun listMyVenues(): List<VenueDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/venues").body()

    override suspend fun listMyEvents(): List<OrgEventItem> =
        httpClient.get("$baseUrl/api/v1/my/organization/events").body()

    override suspend fun addMemberByPhone(phone: String, role: String, venueId: String?): AddMemberByPhoneResponse =
        httpClient.post("$baseUrl/api/v1/my/organization/members/by-phone") {
            contentType(ContentType.Application.Json)
            setBody(AddMemberByPhoneRequest(phone = phone, role = role, venueId = venueId))
        }.body()

    override suspend fun addMember(userId: String, role: String, venueId: String?): OrgMemberDto =
        httpClient.post("$baseUrl/api/v1/my/organization/members") {
            contentType(ContentType.Application.Json)
            setBody(AddMemberRequest(userId = userId, role = role, venueId = venueId))
        }.body()

    override suspend fun updateMember(memberId: String, role: String, venueId: String?): OrgMemberDto =
        httpClient.put("$baseUrl/api/v1/my/organization/members/$memberId") {
            contentType(ContentType.Application.Json)
            setBody(UpdateMemberRequest(role = role, venueId = venueId))
        }.body()

    override suspend fun deleteMember(memberId: String) {
        httpClient.delete("$baseUrl/api/v1/my/organization/members/$memberId")
    }

    override suspend fun leaveOrganization() {
        httpClient.delete("$baseUrl/api/v1/my/organization/membership")
    }
}

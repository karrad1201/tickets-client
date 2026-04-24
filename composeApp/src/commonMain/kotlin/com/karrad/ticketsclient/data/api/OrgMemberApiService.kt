package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AddMemberRequest
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.data.api.dto.OrgMembershipDto
import com.karrad.ticketsclient.data.api.dto.UpdateMemberRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
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

    override suspend fun getMyMembership(authToken: String): OrgMembershipDto? {
        val response = httpClient.get("$baseUrl/api/v1/my/organization/membership") {
            header("Authorization", "Bearer $authToken")
        }
        return if (response.status == HttpStatusCode.NotFound) null
        else response.body()
    }

    override suspend fun listMembers(authToken: String): List<OrgMemberDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/members") {
            header("Authorization", "Bearer $authToken")
        }.body()

    override suspend fun addMember(authToken: String, userId: String, role: String, venueId: String?): OrgMemberDto =
        httpClient.post("$baseUrl/api/v1/my/organization/members") {
            header("Authorization", "Bearer $authToken")
            contentType(ContentType.Application.Json)
            setBody(AddMemberRequest(userId = userId, role = role, venueId = venueId))
        }.body()

    override suspend fun updateMember(authToken: String, memberId: String, role: String, venueId: String?): OrgMemberDto =
        httpClient.put("$baseUrl/api/v1/my/organization/members/$memberId") {
            header("Authorization", "Bearer $authToken")
            contentType(ContentType.Application.Json)
            setBody(UpdateMemberRequest(role = role, venueId = venueId))
        }.body()

    override suspend fun deleteMember(authToken: String, memberId: String) {
        httpClient.delete("$baseUrl/api/v1/my/organization/members/$memberId") {
            header("Authorization", "Bearer $authToken")
        }
    }
}

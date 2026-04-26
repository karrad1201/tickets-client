package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrgMembershipDto(
    val memberId: String,
    val organizationId: String,
    val role: String,
    val venueId: String? = null
)

@Serializable
data class OrgMemberDto(
    val id: String,
    val organizationId: String,
    val userId: String,
    val role: String,
    val venueId: String? = null
)

@Serializable
data class AddMemberRequest(
    val userId: String,
    val role: String,
    val venueId: String? = null
)

@Serializable
data class UpdateMemberRequest(
    val role: String,
    val venueId: String? = null
)

@Serializable
data class AddMemberByPhoneRequest(
    val phone: String,
    val role: String,
    val venueId: String? = null
)

@Serializable
data class AddMemberByPhoneResponse(
    val member: OrgMemberDto,
    val accountCreated: Boolean
)

@Serializable
data class VenueDto(
    val id: String,
    val label: String,
    val address: String? = null,
    val organizationId: String? = null
)

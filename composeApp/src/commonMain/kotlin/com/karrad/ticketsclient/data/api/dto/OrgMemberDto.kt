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

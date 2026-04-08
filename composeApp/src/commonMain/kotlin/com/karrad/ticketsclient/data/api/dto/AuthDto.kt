package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendCodeRequest(val phone: String)

@Serializable
data class LoginRequest(val phone: String, val code: String)

@Serializable
data class RegisterRequest(val phone: String, val code: String, val fullName: String)

@Serializable
data class AuthResponseDto(val token: String, val user: UserDto)

@Serializable
data class UserDto(
    val id: String,
    val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    val role: String = "USER"
)

package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendCodeRequest(val phone: String)

@Serializable
data class LoginRequest(val phone: String, val code: String)

@Serializable
data class RegisterRequest(val phone: String, val code: String)

@Serializable
data class AuthResponseDto(val token: String, val user: UserDto)

@Serializable
data class UserDto(
    val id: String,
    val phone: String,
    val name: String? = null
)

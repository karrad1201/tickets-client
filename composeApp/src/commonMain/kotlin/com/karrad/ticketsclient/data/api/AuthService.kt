package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AuthResponseDto

interface AuthService {
    suspend fun sendCode(phone: String)
    suspend fun login(phone: String, code: String): AuthResponseDto
    suspend fun register(phone: String, code: String, fullName: String): AuthResponseDto
}

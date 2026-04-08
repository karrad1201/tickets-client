package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AuthResponseDto
import com.karrad.ticketsclient.data.api.dto.LoginRequest
import com.karrad.ticketsclient.data.api.dto.RegisterRequest
import com.karrad.ticketsclient.data.api.dto.SendCodeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthService {

    override suspend fun sendCode(phone: String) {
        httpClient.post("$baseUrl/auth/send-code") {
            contentType(ContentType.Application.Json)
            setBody(SendCodeRequest(phone))
        }
    }

    override suspend fun login(phone: String, code: String): AuthResponseDto =
        httpClient.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(phone, code))
        }.body()

    override suspend fun register(phone: String, code: String, fullName: String): AuthResponseDto =
        httpClient.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(phone, code, fullName))
        }.body()
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
private data class UpdateProfileRequest(
    val fullName: String? = null,
    val interests: List<String>? = null
)

class ProfileApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : ProfileService {

    override suspend fun updateProfile(fullName: String?, interests: List<String>?): UserDto =
        httpClient.patch("$baseUrl/auth/me") {
            contentType(ContentType.Application.Json)
            setBody(UpdateProfileRequest(fullName, interests))
        }.body()

    override suspend fun uploadAvatar(imageBytes: ByteArray, extension: String): UserDto =
        httpClient.post("$baseUrl/auth/me/avatar") {
            setBody(MultiPartFormDataContent(formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"avatar.$extension\"")
                    append(HttpHeaders.ContentType, "image/$extension")
                })
            }))
        }.body()
}

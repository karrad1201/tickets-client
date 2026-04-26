package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateVenueApplicationRequest
import com.karrad.ticketsclient.data.api.dto.VenueApplicationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully

class VenueApplicationApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : VenueApplicationService {

    override suspend fun submit(request: CreateVenueApplicationRequest): VenueApplicationDto =
        httpClient.post("$baseUrl/api/v1/my/organization/venue-applications") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun listMine(): List<VenueApplicationDto> =
        httpClient.get("$baseUrl/api/v1/my/organization/venue-applications").body()

    override suspend fun uploadDocuments(applicationId: String, files: List<FileBytes>): VenueApplicationDto =
        httpClient.post("$baseUrl/api/v1/my/organization/venue-applications/$applicationId/documents") {
            setBody(MultiPartFormDataContent(formData {
                files.forEach { f ->
                    appendInput(
                        key = "files",
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"${f.name}\"")
                            append(HttpHeaders.ContentType, f.mimeType)
                        },
                        size = f.bytes.size.toLong()
                    ) {
                        buildPacket { writeFully(f.bytes) }
                    }
                }
            }))
        }.body()
}

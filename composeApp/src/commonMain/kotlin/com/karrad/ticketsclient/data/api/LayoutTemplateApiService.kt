package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateLayoutTemplateRequest
import com.karrad.ticketsclient.data.api.dto.LayoutTemplateDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LayoutTemplateApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : LayoutTemplateService {

    override suspend fun list(venueSpaceId: String): List<LayoutTemplateDto> =
        httpClient.get("$baseUrl/api/v1/layout-templates") {
            parameter("venueSpaceId", venueSpaceId)
        }.body()

    override suspend fun create(request: CreateLayoutTemplateRequest): LayoutTemplateDto =
        httpClient.post("$baseUrl/api/v1/layout-templates") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}

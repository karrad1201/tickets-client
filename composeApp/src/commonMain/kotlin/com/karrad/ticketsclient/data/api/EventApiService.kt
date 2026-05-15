package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AttendeeDto
import com.karrad.ticketsclient.data.api.dto.CreateEventRequest
import com.karrad.ticketsclient.data.api.dto.CreateGeneralAdmissionInventoryRequest
import com.karrad.ticketsclient.data.api.dto.CreateSeatedInventoryRequest
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.EventPhotoDto
import com.karrad.ticketsclient.data.api.dto.SeatMapDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeDto
import com.karrad.ticketsclient.data.api.dto.UpdateEventRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class EventApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : EventService {

    override suspend fun getEvent(eventId: String): EventDto =
        httpClient.get("$baseUrl/api/v1/events/$eventId").body()

    override suspend fun search(
        query: String,
        city: String,
        page: Int,
        dateFrom: String?,
        dateTo: String?,
        categoryIds: List<String>
    ): List<EventDto> =
        httpClient.get("$baseUrl/api/v1/events/search") {
            parameter("q", query)
            parameter("city", city)
            parameter("page", page)
            if (dateFrom != null) parameter("dateFrom", dateFrom)
            if (dateTo != null) parameter("dateTo", dateTo)
            categoryIds.forEach { parameter("categoryId", it) }
        }.body()

    override suspend fun getTicketTypes(eventId: String): List<TicketTypeDto> =
        httpClient.get("$baseUrl/api/v1/inventory/$eventId/ticket-types").body()

    override suspend fun getSeatMap(eventId: String): SeatMapDto =
        httpClient.get("$baseUrl/api/v1/inventory/$eventId/seat-map").body()

    override suspend fun createEvent(request: CreateEventRequest): EventDto =
        httpClient.post("$baseUrl/api/v1/events") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun updateEvent(eventId: String, request: UpdateEventRequest): EventDto =
        httpClient.patch("$baseUrl/api/v1/events/$eventId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun createGeneralAdmissionInventory(eventId: String, request: CreateGeneralAdmissionInventoryRequest) {
        httpClient.post("$baseUrl/api/v1/events/$eventId/inventory/general-admission") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun createSeatedInventory(eventId: String, request: CreateSeatedInventoryRequest) {
        httpClient.post("$baseUrl/api/v1/events/$eventId/inventory/seated") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun uploadCover(eventId: String, file: FileBytes) {
        httpClient.post("$baseUrl/api/v1/events/$eventId/cover") {
            setBody(MultiPartFormDataContent(formData {
                append("file", file.bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    append(HttpHeaders.ContentType, file.mimeType)
                })
            }))
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        httpClient.delete("$baseUrl/api/v1/events/$eventId")
    }

    override suspend fun getPhotos(eventId: String): List<EventPhotoDto> =
        httpClient.get("$baseUrl/api/v1/events/$eventId/photos").body()

    override suspend fun uploadPhoto(eventId: String, file: FileBytes, sortOrder: Int): EventPhotoDto =
        httpClient.post("$baseUrl/api/v1/events/$eventId/photos") {
            setBody(MultiPartFormDataContent(formData {
                append("file", file.bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    append(HttpHeaders.ContentType, file.mimeType)
                })
                append("sortOrder", sortOrder.toString())
            }))
        }.body()

    override suspend fun deletePhoto(eventId: String, photoId: String) {
        httpClient.delete("$baseUrl/api/v1/events/$eventId/photos/$photoId")
    }

    override suspend fun getAttendees(eventId: String, page: Int, size: Int): List<AttendeeDto> =
        httpClient.get("$baseUrl/api/v1/events/$eventId/attendees") {
            parameter("page", page)
            parameter("size", size)
        }.body()
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateOrderRequestDto
import com.karrad.ticketsclient.data.api.dto.OrderDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OrderApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : OrderService {

    override suspend fun createOrder(eventId: String, request: CreateOrderRequestDto): OrderDto =
        httpClient.post("$baseUrl/api/v1/events/$eventId/orders") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun confirmPayment(orderId: String): OrderDto =
        httpClient.post("$baseUrl/api/v1/orders/$orderId/confirm-payment").body()

    override suspend fun getOrder(orderId: String): OrderDto =
        httpClient.get("$baseUrl/api/v1/orders/$orderId").body()
}

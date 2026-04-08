package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrderDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post

class OrderApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : OrderService {

    override suspend fun createOrder(eventId: String, authToken: String): OrderDto =
        httpClient.post("$baseUrl/api/events/$eventId/orders") {
            bearerAuth(authToken)
        }.body()

    override suspend fun confirmPayment(orderId: String, authToken: String): OrderDto =
        httpClient.post("$baseUrl/api/orders/$orderId/confirm-payment") {
            bearerAuth(authToken)
        }.body()

    override suspend fun getOrder(orderId: String, authToken: String): OrderDto =
        httpClient.get("$baseUrl/api/orders/$orderId") {
            bearerAuth(authToken)
        }.body()
}

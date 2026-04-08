package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrderDto

interface OrderService {
    suspend fun createOrder(eventId: String, authToken: String): OrderDto
    suspend fun confirmPayment(orderId: String, authToken: String): OrderDto
    suspend fun getOrder(orderId: String, authToken: String): OrderDto
}

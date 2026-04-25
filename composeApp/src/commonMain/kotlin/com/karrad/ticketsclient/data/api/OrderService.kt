package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateOrderRequestDto
import com.karrad.ticketsclient.data.api.dto.OrderDto

interface OrderService {
    suspend fun createOrder(eventId: String, request: CreateOrderRequestDto): OrderDto
    suspend fun confirmPayment(orderId: String): OrderDto
    suspend fun getOrder(orderId: String): OrderDto
}

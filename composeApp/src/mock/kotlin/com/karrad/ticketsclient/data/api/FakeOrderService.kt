package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CreateOrderRequestDto
import com.karrad.ticketsclient.data.api.dto.OrderDto
import kotlinx.coroutines.delay

/**
 * Мок-реализация для разработки без бекенда.
 * Имитирует успешный заказ с небольшой задержкой.
 */
class FakeOrderService : OrderService {

    override suspend fun createOrder(eventId: String, authToken: String, request: CreateOrderRequestDto): OrderDto {
        delay(800)
        val totalPrice = when {
            request.seatKeys != null -> request.seatKeys.size * 1500
            request.admissionItems != null -> request.admissionItems.sumOf { it.quantity } * 1500
            else -> 1500
        }
        return OrderDto(
            id = "order-fake-${eventId.takeLast(6)}",
            eventId = eventId,
            status = "PENDING_PAYMENT",
            totalPrice = totalPrice,
            amount = totalPrice
        )
    }

    override suspend fun confirmPayment(orderId: String, authToken: String): OrderDto {
        delay(1000)
        return OrderDto(
            id = orderId,
            eventId = "e-fake",
            status = "PAID",
            totalPrice = 1500,
            amount = 1500,
            ticketId = "ticket-fake-001"
        )
    }

    override suspend fun getOrder(orderId: String, authToken: String): OrderDto {
        return OrderDto(
            id = orderId,
            eventId = "e-fake",
            status = "PAID",
            totalPrice = 1500,
            amount = 1500
        )
    }
}

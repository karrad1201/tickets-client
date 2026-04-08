package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.OrderDto
import kotlinx.coroutines.delay

/**
 * Мок-реализация для разработки без бекенда.
 * Имитирует успешный заказ с небольшой задержкой.
 */
class FakeOrderService : OrderService {

    override suspend fun createOrder(eventId: String, authToken: String): OrderDto {
        delay(800)
        return OrderDto(
            id = "order-fake-${eventId.takeLast(6)}",
            eventId = eventId,
            status = "PENDING",
            totalPrice = 1500
        )
    }

    override suspend fun confirmPayment(orderId: String, authToken: String): OrderDto {
        delay(1000)
        return OrderDto(
            id = orderId,
            eventId = "e-fake",
            status = "CONFIRMED",
            totalPrice = 1500,
            ticketId = "ticket-fake-001"
        )
    }

    override suspend fun getOrder(orderId: String, authToken: String): OrderDto {
        return OrderDto(
            id = orderId,
            eventId = "e-fake",
            status = "CONFIRMED",
            totalPrice = 1500
        )
    }
}

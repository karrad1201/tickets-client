package com.karrad.ticketsclient.domain.fake

import com.karrad.ticketsclient.domain.model.AdmissionOrderRequest
import com.karrad.ticketsclient.domain.model.Order
import com.karrad.ticketsclient.domain.model.OrderStatus
import com.karrad.ticketsclient.domain.model.SeatKey
import com.karrad.ticketsclient.domain.repository.OrderRepository
import kotlinx.datetime.Clock

class FakeOrderRepository : OrderRepository {

    val createdOrders = mutableListOf<Order>()

    override suspend fun createSeatedOrder(eventId: String, seatKeys: List<SeatKey>): Order {
        val order = Order(
            id = "order-${createdOrders.size + 1}",
            eventId = eventId,
            amount = seatKeys.size * 100,
            status = OrderStatus.PENDING_PAYMENT,
            paymentUrl = "https://pay.example.com/order-${createdOrders.size + 1}",
            expiresAt = Clock.System.now(),
            createdAt = Clock.System.now()
        )
        createdOrders += order
        return order
    }

    override suspend fun createAdmissionOrder(eventId: String, items: List<AdmissionOrderRequest>): Order {
        val order = Order(
            id = "order-${createdOrders.size + 1}",
            eventId = eventId,
            amount = items.sumOf { it.quantity } * 200,
            status = OrderStatus.PENDING_PAYMENT,
            paymentUrl = "https://pay.example.com/order-${createdOrders.size + 1}",
            expiresAt = Clock.System.now(),
            createdAt = Clock.System.now()
        )
        createdOrders += order
        return order
    }

    override suspend fun getById(id: String): Order =
        createdOrders.first { it.id == id }
}

package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.AdmissionOrderRequest
import com.karrad.ticketsclient.domain.model.InventoryMode
import com.karrad.ticketsclient.domain.model.Order
import com.karrad.ticketsclient.domain.model.SeatKey
import com.karrad.ticketsclient.domain.repository.EventRepository
import com.karrad.ticketsclient.domain.repository.OrderRepository

class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val eventRepository: EventRepository
) {
    suspend fun seated(eventId: String, seatKeys: List<SeatKey>): Order {
        require(seatKeys.isNotEmpty()) { "Seated order requires at least one seat" }
        val event = eventRepository.getById(eventId)
        require(event.inventoryMode == InventoryMode.SEATED) { "Event is not seated" }
        return orderRepository.createSeatedOrder(eventId, seatKeys)
    }

    suspend fun admission(eventId: String, items: List<AdmissionOrderRequest>): Order {
        require(items.isNotEmpty()) { "Admission order requires at least one item" }
        require(items.all { it.quantity > 0 }) { "Each item must have positive quantity" }
        val event = eventRepository.getById(eventId)
        require(event.inventoryMode == InventoryMode.GENERAL_ADMISSION) { "Event is not general admission" }
        return orderRepository.createAdmissionOrder(eventId, items)
    }
}

package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.AdmissionOrderRequest
import com.karrad.ticketsclient.domain.model.Order
import com.karrad.ticketsclient.domain.model.SeatKey

interface OrderRepository {
    suspend fun createSeatedOrder(eventId: String, seatKeys: List<SeatKey>): Order
    suspend fun createAdmissionOrder(eventId: String, items: List<AdmissionOrderRequest>): Order
    suspend fun getById(id: String): Order
}

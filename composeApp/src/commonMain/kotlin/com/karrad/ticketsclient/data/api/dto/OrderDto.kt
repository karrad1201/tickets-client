package com.karrad.ticketsclient.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val eventId: String,
    val status: String, // PENDING_PAYMENT, PAID, PAYMENT_FAILED, EXPIRED
    val totalPrice: Int = 0,
    val amount: Int? = null,
    val ticketId: String? = null
)

@Serializable
data class CreateOrderRequestDto(
    val seatKeys: List<SeatKeyRequestDto>? = null,
    val admissionItems: List<AdmissionInventoryItemRequestDto>? = null
)

@Serializable
data class SeatKeyRequestDto(
    val sectionKey: String,
    val rowKey: String,
    val seatKey: String
)

@Serializable
data class AdmissionInventoryItemRequestDto(
    val ticketTypeId: String,
    val quantity: Int
)
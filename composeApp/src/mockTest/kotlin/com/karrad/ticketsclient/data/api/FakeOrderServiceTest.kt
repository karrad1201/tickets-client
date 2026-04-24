package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AdmissionInventoryItemRequestDto
import com.karrad.ticketsclient.data.api.dto.CreateOrderRequestDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FakeOrderServiceTest {

    private val service = FakeOrderService()
    private val request = CreateOrderRequestDto(
        admissionItems = listOf(AdmissionInventoryItemRequestDto(ticketTypeId = "tt-general", quantity = 1))
    )

    @Test
    fun `createOrder returns order with correct eventId`() = runTest {
        val order = service.createOrder("evt-001", "token", request)
        assertEquals("evt-001", order.eventId)
    }

    @Test
    fun `createOrder returns pending payment status`() = runTest {
        val order = service.createOrder("evt-001", "token", request)
        assertEquals("PENDING_PAYMENT", order.status)
    }

    @Test
    fun `createOrder returns non-blank orderId`() = runTest {
        val order = service.createOrder("evt-001", "token", request)
        assertTrue(order.id.isNotBlank())
    }

    @Test
    fun `createOrder returns positive totalPrice`() = runTest {
        val order = service.createOrder("evt-001", "token", request)
        assertTrue(order.totalPrice > 0)
    }

    @Test
    fun `confirmPayment returns paid status`() = runTest {
        val created = service.createOrder("evt-001", "token", request)
        val confirmed = service.confirmPayment(created.id, "token")
        assertEquals("PAID", confirmed.status)
    }

    @Test
    fun `confirmPayment returns ticketId`() = runTest {
        val created = service.createOrder("evt-001", "token", request)
        val confirmed = service.confirmPayment(created.id, "token")
        assertNotNull(confirmed.ticketId)
        assertTrue(confirmed.ticketId.isNotBlank())
    }
}
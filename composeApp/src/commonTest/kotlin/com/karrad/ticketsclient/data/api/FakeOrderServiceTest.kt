package com.karrad.ticketsclient.data.api

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FakeOrderServiceTest {

    private val service = FakeOrderService()

    @Test
    fun `createOrder returns order with correct eventId`() = runTest {
        val order = service.createOrder("evt-001", "token")
        assertEquals("evt-001", order.eventId)
    }

    @Test
    fun `createOrder returns PENDING status`() = runTest {
        val order = service.createOrder("evt-001", "token")
        assertEquals("PENDING", order.status)
    }

    @Test
    fun `createOrder returns non-blank orderId`() = runTest {
        val order = service.createOrder("evt-001", "token")
        assertTrue(order.id.isNotBlank())
    }

    @Test
    fun `createOrder returns positive totalPrice`() = runTest {
        val order = service.createOrder("evt-001", "token")
        assertTrue(order.totalPrice > 0)
    }

    @Test
    fun `confirmPayment returns CONFIRMED status`() = runTest {
        val created = service.createOrder("evt-001", "token")
        val confirmed = service.confirmPayment(created.id, "token")
        assertEquals("CONFIRMED", confirmed.status)
    }

    @Test
    fun `confirmPayment returns ticketId`() = runTest {
        val created = service.createOrder("evt-001", "token")
        val confirmed = service.confirmPayment(created.id, "token")
        assertNotNull(confirmed.ticketId)
        assertTrue(confirmed.ticketId.isNotBlank())
    }
}

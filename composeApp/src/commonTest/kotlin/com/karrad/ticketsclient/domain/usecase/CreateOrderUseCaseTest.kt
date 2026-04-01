package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.fake.FakeEventRepository
import com.karrad.ticketsclient.domain.fake.FakeOrderRepository
import com.karrad.ticketsclient.domain.fake.testEvent
import com.karrad.ticketsclient.domain.model.AdmissionOrderRequest
import com.karrad.ticketsclient.domain.model.InventoryMode
import com.karrad.ticketsclient.domain.model.OrderStatus
import com.karrad.ticketsclient.domain.model.SeatKey
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateOrderUseCaseTest {

    private val seatedEvent = testEvent(id = "seated-event", inventoryMode = InventoryMode.SEATED)
    private val admissionEvent = testEvent(id = "admission-event", inventoryMode = InventoryMode.GENERAL_ADMISSION)

    private val eventRepo = FakeEventRepository(events = mutableListOf(seatedEvent, admissionEvent))
    private val orderRepo = FakeOrderRepository()
    private val useCase = CreateOrderUseCase(orderRepo, eventRepo)

    // --- seated ---

    @Test
    fun `seated order is created and returned`() = runTest {
        val seats = listOf(SeatKey("A", "1", "5"), SeatKey("A", "1", "6"))

        val order = useCase.seated("seated-event", seats)

        assertEquals(OrderStatus.PENDING_PAYMENT, order.status)
        assertEquals("seated-event", order.eventId)
        assertEquals(1, orderRepo.createdOrders.size)
    }

    @Test
    fun `seated throws when seatKeys is empty`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.seated("seated-event", emptyList())
        }
    }

    @Test
    fun `seated throws when event is general admission`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.seated("admission-event", listOf(SeatKey("A", "1", "1")))
        }
    }

    // --- admission ---

    @Test
    fun `admission order is created and returned`() = runTest {
        val items = listOf(AdmissionOrderRequest("type-1", quantity = 2))

        val order = useCase.admission("admission-event", items)

        assertEquals(OrderStatus.PENDING_PAYMENT, order.status)
        assertEquals("admission-event", order.eventId)
        assertEquals(1, orderRepo.createdOrders.size)
    }

    @Test
    fun `admission throws when items is empty`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.admission("admission-event", emptyList())
        }
    }

    @Test
    fun `admission throws when item quantity is zero`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.admission("admission-event", listOf(AdmissionOrderRequest("type-1", quantity = 0)))
        }
    }

    @Test
    fun `admission throws when event is seated`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase.admission("seated-event", listOf(AdmissionOrderRequest("type-1", quantity = 1)))
        }
    }
}

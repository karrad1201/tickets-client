package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.fake.FakeEventRepository
import com.karrad.ticketsclient.domain.fake.testEvent
import com.karrad.ticketsclient.domain.model.EventSearchFilter
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchEventsUseCaseTest {

    private val events = mutableListOf(
        testEvent(id = "e-1", label = "Лебединое озеро"),
        testEvent(id = "e-2", label = "Новогодний концерт"),
        testEvent(id = "e-3", label = "Гамлет")
    )
    private val useCase = SearchEventsUseCase(FakeEventRepository(events = events))

    @Test
    fun `empty filter returns all events`() = runTest {
        val result = useCase(EventSearchFilter())

        assertEquals(3, result.size)
    }

    @Test
    fun `filters by query case insensitive`() = runTest {
        val result = useCase(EventSearchFilter(query = "лебед"))

        assertEquals(1, result.size)
        assertEquals("e-1", result.first().id)
    }

    @Test
    fun `returns empty list when no events match query`() = runTest {
        val result = useCase(EventSearchFilter(query = "балет"))

        assertTrue(result.isEmpty())
    }

    @Test
    fun `filters by category`() = runTest {
        val result = useCase(EventSearchFilter(categoryIds = listOf("cat-1")))

        assertEquals(3, result.size)
    }

    @Test
    fun `returns empty when category does not match`() = runTest {
        val result = useCase(EventSearchFilter(categoryIds = listOf("cat-999")))

        assertTrue(result.isEmpty())
    }
}

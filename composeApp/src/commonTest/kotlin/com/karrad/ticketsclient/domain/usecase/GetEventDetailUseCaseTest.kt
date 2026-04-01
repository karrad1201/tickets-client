package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.fake.FakeEventRepository
import com.karrad.ticketsclient.domain.fake.testEvent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetEventDetailUseCaseTest {

    @Test
    fun `returns event by id`() = runTest {
        val event = testEvent(id = "event-42")
        val useCase = GetEventDetailUseCase(FakeEventRepository(events = mutableListOf(event)))

        val result = useCase("event-42")

        assertEquals("event-42", result.id)
    }

    @Test
    fun `throws when event not found`() = runTest {
        val useCase = GetEventDetailUseCase(FakeEventRepository())

        assertFailsWith<NoSuchElementException> {
            useCase("nonexistent")
        }
    }
}

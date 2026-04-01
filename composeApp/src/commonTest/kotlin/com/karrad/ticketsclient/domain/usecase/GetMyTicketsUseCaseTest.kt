package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.fake.FakeTicketRepository
import com.karrad.ticketsclient.domain.fake.testTicket
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMyTicketsUseCaseTest {

    @Test
    fun `returns all user tickets`() = runTest {
        val tickets = listOf(testTicket("t-1"), testTicket("t-2"))
        val useCase = GetMyTicketsUseCase(FakeTicketRepository(tickets))

        val result = useCase()

        assertEquals(2, result.size)
    }

    @Test
    fun `returns empty list when no tickets`() = runTest {
        val useCase = GetMyTicketsUseCase(FakeTicketRepository())

        val result = useCase()

        assertTrue(result.isEmpty())
    }
}

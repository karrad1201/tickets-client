package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.fake.FakeEventRepository
import com.karrad.ticketsclient.domain.fake.testEvent
import com.karrad.ticketsclient.domain.model.DiscoveryFeed
import com.karrad.ticketsclient.domain.fake.testCategory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDiscoveryFeedUseCaseTest {

    @Test
    fun `returns feed from repository`() = runTest {
        val event = testEvent()
        val feed = DiscoveryFeed(
            forYou = listOf(event),
            byCategory = mapOf(testCategory to listOf(event)),
            tomorrow = emptyList(),
            dayAfterTomorrow = emptyList()
        )
        val useCase = GetDiscoveryFeedUseCase(FakeEventRepository(feed = feed))

        val result = useCase()

        assertEquals(1, result.forYou.size)
        assertEquals(event.id, result.forYou.first().id)
    }
}

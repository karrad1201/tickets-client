package com.karrad.ticketsclient.data.api

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FakeDiscoveryApiServiceTest {

    private val service = FakeDiscoveryApiService()

    @Test
    fun `feed contains events with hasSeatMap true`() = runTest {
        val feed = service.getDiscoveryFeed(city = "Москва")
        val allEvents = feed.forYou + feed.tomorrow + feed.dayAfterTomorrow +
            feed.byCategory.flatMap { it.events }

        assertTrue(
            allEvents.any { it.hasSeatMap },
            "Лента должна содержать хотя бы одно событие с рассадкой мест (hasSeatMap=true)"
        )
    }

    @Test
    fun `feed contains events with hasSeatMap false`() = runTest {
        val feed = service.getDiscoveryFeed(city = "Москва")
        val allEvents = feed.forYou + feed.tomorrow + feed.dayAfterTomorrow +
            feed.byCategory.flatMap { it.events }

        assertTrue(
            allEvents.any { !it.hasSeatMap },
            "Лента должна содержать хотя бы одно событие без рассадки мест (hasSeatMap=false)"
        )
    }

    @Test
    fun `forYou is not empty`() = runTest {
        val feed = service.getDiscoveryFeed(city = "Москва")
        assertTrue(feed.forYou.isNotEmpty())
    }

    @Test
    fun `byCategory is not empty`() = runTest {
        val feed = service.getDiscoveryFeed(city = "Москва")
        assertTrue(feed.byCategory.isNotEmpty())
    }

    @Test
    fun `all events have non-blank label and id`() = runTest {
        val feed = service.getDiscoveryFeed(city = "Москва")
        val allEvents = feed.forYou + feed.tomorrow + feed.dayAfterTomorrow +
            feed.byCategory.flatMap { it.events }

        allEvents.forEach { event ->
            assertTrue(event.id.isNotBlank(), "id не должен быть пустым")
            assertTrue(event.label.isNotBlank(), "label не должен быть пустым")
        }
    }
}

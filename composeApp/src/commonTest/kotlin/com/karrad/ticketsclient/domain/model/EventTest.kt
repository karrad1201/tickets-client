package com.karrad.ticketsclient.domain.model

import com.karrad.ticketsclient.domain.fake.testEvent
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventTest {

    @Test
    fun `isSalesClosed returns false when time is in the future and no salesClosedAt`() {
        val event = testEvent(
            time = Instant.parse("2030-01-01T00:00:00Z"),
            salesClosedAt = null
        )
        val now = Instant.parse("2026-01-01T00:00:00Z")

        assertFalse(event.isSalesClosed(now))
    }

    @Test
    fun `isSalesClosed returns true when salesClosedAt is set`() {
        val event = testEvent(
            time = Instant.parse("2030-01-01T00:00:00Z"),
            salesClosedAt = Instant.parse("2029-12-31T00:00:00Z")
        )
        val now = Instant.parse("2026-01-01T00:00:00Z")

        assertTrue(event.isSalesClosed(now))
    }

    @Test
    fun `isSalesClosed returns true when event time is in the past`() {
        val event = testEvent(
            time = Instant.parse("2020-01-01T00:00:00Z"),
            salesClosedAt = null
        )
        val now = Instant.parse("2026-01-01T00:00:00Z")

        assertTrue(event.isSalesClosed(now))
    }

    @Test
    fun `isSalesClosed returns true when now equals event time`() {
        val time = Instant.parse("2026-06-01T18:00:00Z")
        val event = testEvent(time = time, salesClosedAt = null)

        assertTrue(event.isSalesClosed(time))
    }
}

package com.karrad.ticketsclient.data.dto

import com.karrad.ticketsclient.data.api.dto.EventDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `hasSeatMap defaults to false`() {
        val event = EventDto(
            id = "e-1",
            label = "Test",
            description = "desc",
            venueId = "venue-1",
            categoryId = "cat-1",
            time = "2026-06-01T18:00:00Z"
        )
        assertFalse(event.hasSeatMap)
    }

    @Test
    fun `hasSeatMap can be set to true`() {
        val event = EventDto(
            id = "e-1",
            label = "Test",
            description = "desc",
            venueId = "venue-1",
            categoryId = "cat-1",
            time = "2026-06-01T18:00:00Z",
            hasSeatMap = true
        )
        assertTrue(event.hasSeatMap)
    }

    @Test
    fun `deserializes hasSeatMap from JSON when present`() {
        val jsonStr = """
            {
              "id": "e-1",
              "label": "Test",
              "description": "desc",
              "venueId": "venue-1",
              "categoryId": "cat-1",
              "time": "2026-06-01T18:00:00Z",
              "hasSeatMap": true
            }
        """.trimIndent()

        val event = json.decodeFromString<EventDto>(jsonStr)
        assertTrue(event.hasSeatMap)
    }

    @Test
    fun `hasSeatMap defaults to false when missing from JSON`() {
        val jsonStr = """
            {
              "id": "e-1",
              "label": "Test",
              "description": "desc",
              "venueId": "venue-1",
              "categoryId": "cat-1",
              "time": "2026-06-01T18:00:00Z"
            }
        """.trimIndent()

        val event = json.decodeFromString<EventDto>(jsonStr)
        assertFalse(event.hasSeatMap)
    }

    @Test
    fun `optional fields are null by default`() {
        val event = EventDto(
            id = "e-1",
            label = "Test",
            description = "desc",
            venueId = "venue-1",
            categoryId = "cat-1",
            time = "2026-06-01T18:00:00Z"
        )
        assertEquals(null, event.imageUrl)
        assertEquals(null, event.minPrice)
        assertEquals(null, event.ageRating)
        assertEquals(null, event.organizationId)
        assertEquals(null, event.salesClosedAt)
    }
}

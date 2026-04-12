package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.SeatItemDto
import com.karrad.ticketsclient.data.api.dto.SeatMapDto
import com.karrad.ticketsclient.data.api.dto.SeatRowDto
import com.karrad.ticketsclient.data.api.dto.SeatSectionDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeDto

/**
 * Мок-реализация для разработки без бекенда.
 * Использует те же события, что и FakeDiscoveryApiService.
 */
class FakeEventService : EventService {

    private val allEvents: List<EventDto> by lazy {
        val feed = FakeDiscoveryApiService.FEED
        (feed.forYou + feed.tomorrow + feed.dayAfterTomorrow +
            feed.byCategory.flatMap { it.events }).distinctBy { it.id }
    }

    override suspend fun getEvent(eventId: String): EventDto =
        allEvents.find { it.id == eventId } ?: error("Event not found: $eventId")

    override suspend fun search(query: String, city: String, page: Int): List<EventDto> =
        if (query.length < 2) emptyList()
        else allEvents.filter {
            it.label.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }

    override suspend fun getTicketTypes(eventId: String): List<TicketTypeDto> = listOf(
        TicketTypeDto("tt-1", "Входной билет", 500, 100, 87),
        TicketTypeDto("tt-2", "Льготный", 250, 50, 42),
        TicketTypeDto("tt-3", "Семейный", 1200, 20, 15)
    )

    override suspend fun getSeatMap(eventId: String): SeatMapDto {
        val rows = (0..7).map { row ->
            SeatRowDto(
                key = "row-$row",
                label = ('A' + row).toString(),
                seats = (0..9).map { col ->
                    val available = !((row == 2 && col in 3..5) ||
                            (row == 5 && col in 6..8) ||
                            (row == 1 && col == 8) ||
                            (row == 4 && col == 2) ||
                            (row == 6 && col in 0..1) ||
                            (row == 7 && col in 7..9))
                    SeatItemDto(key = "${('A' + row)}${col + 1}", price = 1400, available = available)
                }
            )
        }
        return SeatMapDto(sections = listOf(SeatSectionDto(key = "main", label = "Партер", rows = rows)))
    }
}

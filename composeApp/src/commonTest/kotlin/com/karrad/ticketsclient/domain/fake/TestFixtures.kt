package com.karrad.ticketsclient.domain.fake

import com.karrad.ticketsclient.domain.model.Category
import com.karrad.ticketsclient.domain.model.City
import com.karrad.ticketsclient.domain.model.Event
import com.karrad.ticketsclient.domain.model.InventoryMode
import com.karrad.ticketsclient.domain.model.Subject
import com.karrad.ticketsclient.domain.model.Ticket
import com.karrad.ticketsclient.domain.model.Venue
import kotlinx.datetime.Instant

val testSubject = Subject(id = "s-kal", label = "Калмыкия")
val testCity = City(id = "c-eli", label = "Элиста", subject = testSubject)
val testVenue = Venue(id = "venue-1", label = "Театр", address = "ул. Пушкина, 1", city = testCity)
val testCategory = Category(id = "cat-1", code = "THEATRE", label = "Театры")

fun testEvent(
    id: String = "event-1",
    label: String = "Лебединое озеро",
    inventoryMode: InventoryMode = InventoryMode.GENERAL_ADMISSION,
    time: Instant = Instant.parse("2030-06-01T18:00:00Z"),
    salesClosedAt: Instant? = null
) = Event(
    id = id,
    label = label,
    description = "Описание мероприятия",
    venue = testVenue,
    category = testCategory,
    time = time,
    inventoryMode = inventoryMode,
    minPrice = 400,
    salesClosedAt = salesClosedAt
)

fun testTicket(
    id: String = "ticket-1",
    orderId: String = "order-1",
    eventId: String = "event-1",
    ticketTypeId: String = "type-1"
) = Ticket(
    id = id,
    orderId = orderId,
    eventId = eventId,
    price = 400,
    issuedAt = Instant.parse("2026-03-01T10:00:00Z"),
    ticketTypeId = ticketTypeId
)

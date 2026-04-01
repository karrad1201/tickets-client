package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CategoryEventsEntryDto
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import com.karrad.ticketsclient.data.api.dto.EventDto

class FakeDiscoveryApiService : DiscoveryService {

    override suspend fun getDiscoveryFeed(
        city: String,
        authToken: String?,
        page: Int,
        size: Int
    ): DiscoveryFeedResponseDto {
        return FEED
    }

    companion object {
        private val catTheatre  = CategoryDto("cat-theatre",  "theatre",  "Театры")
        private val catCinema   = CategoryDto("cat-cinema",   "cinema",   "Кино")
        private val catConcert  = CategoryDto("cat-concert",  "concert",  "Концерты")
        private val catStandup  = CategoryDto("cat-standup",  "standup",  "Стендап")
        private val catExhibit  = CategoryDto("cat-exhibit",  "exhibit",  "Выставки")

        // ---- forYou ----
        private val swanLake = EventDto(
            id = "evt-001",
            label = "Лебединое озеро",
            description = "Великий балет Чайковского в исполнении труппы Большого театра",
            venueId = "venue-bolshoi",
            categoryId = "cat-theatre",
            time = "2026-04-05T15:00:00Z",
            minPrice = 2000
        )
        private val newYearParty = EventDto(
            id = "evt-002",
            label = "Новогодняя вечеринка",
            description = "Грандиозная вечеринка с живым звуком и фейерверком",
            venueId = "venue-arena",
            categoryId = "cat-concert",
            time = "2026-04-06T17:00:00Z",
            minPrice = 1500
        )
        private val sleepingBeauty = EventDto(
            id = "evt-003",
            label = "Спящая красавица",
            description = "Классический балет в современной постановке",
            venueId = "venue-bolshoi",
            categoryId = "cat-theatre",
            time = "2026-04-07T14:00:00Z",
            minPrice = 1800
        )

        // ---- tomorrow (2026-04-02) ----
        private val russianBeauty = EventDto(
            id = "evt-011",
            label = "Красота по-русски",
            description = "Авторский спектакль о любви и традициях",
            venueId = "venue-theater",
            categoryId = "cat-theatre",
            time = "2026-04-02T15:00:00Z",
            minPrice = 800
        )
        private val blackPanther = EventDto(
            id = "evt-012",
            label = "Чёрная пантера: Навсегда",
            description = "Финальная глава истории Ваканды",
            venueId = "venue-cinema",
            categoryId = "cat-cinema",
            time = "2026-04-02T16:30:00Z",
            minPrice = 350
        )

        // ---- dayAfterTomorrow (2026-04-03) ----
        private val nineties = EventDto(
            id = "evt-021",
            label = "Вечеринка 90-х",
            description = "Хиты, которые мы помним — живой звук и безумные танцы",
            venueId = "venue-arena",
            categoryId = "cat-concert",
            time = "2026-04-03T18:00:00Z",
            minPrice = 1200
        )

        // ---- byCategory — Theatre ----
        private val nutcracker = EventDto(
            id = "evt-031",
            label = "Щелкунчик",
            description = "Рождественская сказка для всей семьи",
            venueId = "venue-bolshoi",
            categoryId = "cat-theatre",
            time = "2026-04-08T13:00:00Z",
            minPrice = 1600
        )

        // ---- byCategory — Cinema ----
        private val dune2 = EventDto(
            id = "evt-041",
            label = "Дюна: Часть вторая",
            description = "Эпическое продолжение сагмы Вильнёва",
            venueId = "venue-cinema",
            categoryId = "cat-cinema",
            time = "2026-04-04T14:00:00Z",
            minPrice = 350
        )
        private val oppenheimer = EventDto(
            id = "evt-042",
            label = "Оппенгеймер",
            description = "Биографическая драма Кристофера Нолана",
            venueId = "venue-cinema",
            categoryId = "cat-cinema",
            time = "2026-04-04T17:00:00Z",
            minPrice = 350
        )

        // ---- byCategory — Standup ----
        private val standupEvening = EventDto(
            id = "evt-051",
            label = "Вечер стендапа",
            description = "Лучшие комики страны на одной сцене",
            venueId = "venue-club",
            categoryId = "cat-standup",
            time = "2026-04-05T19:00:00Z",
            minPrice = 900
        )
        private val standupBattle = EventDto(
            id = "evt-052",
            label = "Стендап-баттл",
            description = "Кто смешнее? Голосуй за любимого комика",
            venueId = "venue-club",
            categoryId = "cat-standup",
            time = "2026-04-09T19:00:00Z",
            minPrice = 700
        )

        // ---- byCategory — Exhibit ----
        private val aiExhibit = EventDto(
            id = "evt-061",
            label = "Искусственный интеллект: выставка будущего",
            description = "Интерактивная выставка о технологиях и творчестве",
            venueId = "venue-museum",
            categoryId = "cat-exhibit",
            time = "2026-04-10T10:00:00Z",
            minPrice = 400
        )

        val FEED = DiscoveryFeedResponseDto(
            forYou = listOf(swanLake, newYearParty, sleepingBeauty),
            tomorrow = listOf(russianBeauty, blackPanther),
            dayAfterTomorrow = listOf(nineties),
            byCategory = listOf(
                CategoryEventsEntryDto(
                    category = catTheatre,
                    events = listOf(swanLake, sleepingBeauty, russianBeauty, nutcracker)
                ),
                CategoryEventsEntryDto(
                    category = catCinema,
                    events = listOf(blackPanther, dune2, oppenheimer)
                ),
                CategoryEventsEntryDto(
                    category = catConcert,
                    events = listOf(newYearParty, nineties)
                ),
                CategoryEventsEntryDto(
                    category = catStandup,
                    events = listOf(standupEvening, standupBattle)
                ),
                CategoryEventsEntryDto(
                    category = catExhibit,
                    events = listOf(aiExhibit)
                )
            )
        )
    }
}

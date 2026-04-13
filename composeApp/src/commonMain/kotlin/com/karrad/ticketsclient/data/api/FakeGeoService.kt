package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CityDto

/**
 * Мок-реализация для разработки без бекенда.
 */
class FakeGeoService : GeoService {

    override suspend fun getCities(): List<CityDto> = listOf(
        CityDto("c-msk",   "Москва",           "Москва"),
        CityDto("c-spb",   "Санкт-Петербург",  "Санкт-Петербург"),
        CityDto("c-kzn",   "Казань",           "Республика Татарстан"),
        CityDto("c-ufa",   "Уфа",              "Республика Башкортостан"),
        CityDto("c-ekt",   "Екатеринбург",     "Свердловская область"),
        CityDto("c-nvs",   "Новосибирск",      "Новосибирская область"),
        CityDto("c-krd",   "Краснодар",        "Краснодарский край"),
        CityDto("c-sochi", "Сочи",             "Краснодарский край"),
        CityDto("c-ros",   "Ростов-на-Дону",   "Ростовская область"),
        CityDto("c-nnv",   "Нижний Новгород",  "Нижегородская область"),
        CityDto("c-sam",   "Самара",           "Самарская область"),
        CityDto("c-chel",  "Челябинск",        "Челябинская область"),
        CityDto("c-omsk",  "Омск",             "Омская область"),
        CityDto("c-krs",   "Красноярск",       "Красноярский край"),
        CityDto("c-prm",   "Пермь",            "Пермский край"),
        CityDto("c-vrn",   "Воронеж",          "Воронежская область"),
        CityDto("c-sar",   "Саратов",          "Саратовская область"),
        CityDto("c-tym",   "Тюмень",           "Тюменская область"),
        CityDto("c-vlk",   "Владивосток",      "Приморский край"),
        CityDto("c-irk",   "Иркутск",          "Иркутская область"),
        CityDto("c-vlg",   "Волгоград",        "Волгоградская область"),
        CityDto("c-ast",   "Астрахань",        "Астраханская область"),
        CityDto("c-eli",   "Элиста",           "Республика Калмыкия")
    )

    override suspend fun getCategories(): List<CategoryDto> =
        FakeDiscoveryApiService.FEED.byCategory.map { it.category }.distinctBy { it.id }
}

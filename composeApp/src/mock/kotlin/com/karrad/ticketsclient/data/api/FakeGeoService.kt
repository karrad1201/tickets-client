package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CityDto
import com.karrad.ticketsclient.data.api.dto.SubjectDto

/**
 * Мок-реализация для разработки без бекенда.
 */
class FakeGeoService : GeoService {

    override suspend fun getCities(): List<CityDto> = listOf(
        CityDto("c-msk",   "Москва",           SubjectDto("s-msk",  "Москва")),
        CityDto("c-spb",   "Санкт-Петербург",  SubjectDto("s-spb",  "Санкт-Петербург")),
        CityDto("c-kzn",   "Казань",           SubjectDto("s-tat",  "Республика Татарстан")),
        CityDto("c-ufa",   "Уфа",              SubjectDto("s-bash", "Республика Башкортостан")),
        CityDto("c-ekt",   "Екатеринбург",     SubjectDto("s-svr",  "Свердловская область")),
        CityDto("c-nvs",   "Новосибирск",      SubjectDto("s-nvs",  "Новосибирская область")),
        CityDto("c-krd",   "Краснодар",        SubjectDto("s-krd",  "Краснодарский край")),
        CityDto("c-sochi", "Сочи",             SubjectDto("s-krd",  "Краснодарский край")),
        CityDto("c-ros",   "Ростов-на-Дону",   SubjectDto("s-ros",  "Ростовская область")),
        CityDto("c-nnv",   "Нижний Новгород",  SubjectDto("s-nnv",  "Нижегородская область")),
        CityDto("c-sam",   "Самара",           SubjectDto("s-sam",  "Самарская область")),
        CityDto("c-chel",  "Челябинск",        SubjectDto("s-chel", "Челябинская область")),
        CityDto("c-omsk",  "Омск",             SubjectDto("s-omsk", "Омская область")),
        CityDto("c-krs",   "Красноярск",       SubjectDto("s-krs",  "Красноярский край")),
        CityDto("c-prm",   "Пермь",            SubjectDto("s-prm",  "Пермский край")),
        CityDto("c-vrn",   "Воронеж",          SubjectDto("s-vrn",  "Воронежская область")),
        CityDto("c-sar",   "Саратов",          SubjectDto("s-sar",  "Саратовская область")),
        CityDto("c-tym",   "Тюмень",           SubjectDto("s-tym",  "Тюменская область")),
        CityDto("c-vlk",   "Владивосток",      SubjectDto("s-prm2", "Приморский край")),
        CityDto("c-irk",   "Иркутск",          SubjectDto("s-irk",  "Иркутская область")),
        CityDto("c-vlg",   "Волгоград",        SubjectDto("s-vlg",  "Волгоградская область")),
        CityDto("c-ast",   "Астрахань",        SubjectDto("s-ast",  "Астраханская область")),
        CityDto("c-eli",   "Элиста",           SubjectDto("s-kalm", "Республика Калмыкия"))
    )

    override suspend fun getCategories(): List<CategoryDto> =
        FakeDiscoveryApiService.FEED.byCategory.map { it.category }.distinctBy { it.id }
}

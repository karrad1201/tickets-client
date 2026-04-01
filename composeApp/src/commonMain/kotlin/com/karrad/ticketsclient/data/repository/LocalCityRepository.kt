package com.karrad.ticketsclient.data.repository

import com.karrad.ticketsclient.domain.model.City
import com.karrad.ticketsclient.domain.model.Subject
import com.karrad.ticketsclient.domain.repository.CityRepository

/**
 * In-memory city repository with bundled reference data.
 *
 * TODO: When the backend adds GET /geo/cities, create RemoteCityRepository
 *  that fetches and caches the list, then replace this implementation in DI.
 */
class LocalCityRepository : CityRepository {

    private val cities: List<City> = buildCities()

    override fun getAll(): List<City> = cities

    override fun search(query: String): List<City> {
        if (query.isBlank()) return cities
        val q = query.trim().lowercase()
        return cities.filter { city ->
            city.label.lowercase().contains(q) ||
                city.subject.label.lowercase().contains(q)
        }
    }
}

private fun buildCities(): List<City> {
    fun subject(id: String, label: String) = Subject(id = id, label = label)
    fun city(id: String, label: String, subject: Subject) = City(id = id, label = label, subject = subject)

    val moscow       = subject("s-msk",  "Москва")
    val spb          = subject("s-spb",  "Санкт-Петербург")
    val kalmykia     = subject("s-kal",  "Республика Калмыкия")
    val tatarstan    = subject("s-tat",  "Республика Татарстан")
    val bashkortostan= subject("s-bas",  "Республика Башкортостан")
    val krasnodar    = subject("s-krd",  "Краснодарский край")
    val rostov       = subject("s-ros",  "Ростовская область")
    val volgograd    = subject("s-vlg",  "Волгоградская область")
    val astrakhan    = subject("s-ast",  "Астраханская область")
    val novosibirsk  = subject("s-nvs",  "Новосибирская область")
    val ekaterinburg = subject("s-svr",  "Свердловская область")
    val nizhegorodsk = subject("s-nnv",  "Нижегородская область")
    val samara       = subject("s-sam",  "Самарская область")
    val chelyabinsk  = subject("s-chel", "Челябинская область")
    val omsk         = subject("s-omsk", "Омская область")
    val krasnoyarsk  = subject("s-krs",  "Красноярский край")
    val perm         = subject("s-prm",  "Пермский край")
    val voronezh     = subject("s-vrn",  "Воронежская область")
    val saratov      = subject("s-sar",  "Саратовская область")
    val tyumen       = subject("s-tym",  "Тюменская область")
    val ufa          = subject("s-ufa",  "Республика Башкортостан")
    val vladivostok  = subject("s-pri",  "Приморский край")
    val irkutsk      = subject("s-irk",  "Иркутская область")

    return listOf(
        city("c-msk",   "Москва",          moscow),
        city("c-spb",   "Санкт-Петербург", spb),
        city("c-eli",   "Элиста",          kalmykia),
        city("c-kzn",   "Казань",          tatarstan),
        city("c-ufa",   "Уфа",             bashkortostan),
        city("c-krd",   "Краснодар",       krasnodar),
        city("c-sochi", "Сочи",            krasnodar),
        city("c-ros",   "Ростов-на-Дону",  rostov),
        city("c-vlg",   "Волгоград",       volgograd),
        city("c-ast",   "Астрахань",       astrakhan),
        city("c-nvs",   "Новосибирск",     novosibirsk),
        city("c-ekt",   "Екатеринбург",    ekaterinburg),
        city("c-nnv",   "Нижний Новгород", nizhegorodsk),
        city("c-sam",   "Самара",          samara),
        city("c-chel",  "Челябинск",       chelyabinsk),
        city("c-omsk",  "Омск",            omsk),
        city("c-krs",   "Красноярск",      krasnoyarsk),
        city("c-prm",   "Пермь",           perm),
        city("c-vrn",   "Воронеж",         voronezh),
        city("c-sar",   "Саратов",         saratov),
        city("c-tym",   "Тюмень",          tyumen),
        city("c-vlk",   "Владивосток",     vladivostok),
        city("c-irk",   "Иркутск",         irkutsk),
    )
}

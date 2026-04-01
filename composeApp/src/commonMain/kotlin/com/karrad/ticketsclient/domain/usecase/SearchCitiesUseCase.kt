package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.City
import com.karrad.ticketsclient.domain.repository.CityRepository

class SearchCitiesUseCase(private val repository: CityRepository) {
    operator fun invoke(query: String): List<City> = repository.search(query)
}

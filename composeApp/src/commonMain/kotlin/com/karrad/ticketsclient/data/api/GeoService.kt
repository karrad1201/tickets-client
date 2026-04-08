package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CityDto

interface GeoService {
    suspend fun getCities(): List<CityDto>
    suspend fun getCategories(): List<CategoryDto>
}

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.CategoryDto
import com.karrad.ticketsclient.data.api.dto.CityDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GeoApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : GeoService {

    override suspend fun getCities(): List<CityDto> =
        httpClient.get("$baseUrl/api/geo/cities").body()

    override suspend fun getCategories(): List<CategoryDto> =
        httpClient.get("$baseUrl/api/categories").body()
}

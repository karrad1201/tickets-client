package com.karrad.ticketsclient.di

import com.karrad.ticketsclient.data.api.DiscoveryApiService
import com.karrad.ticketsclient.data.api.createHttpClient
import com.karrad.ticketsclient.data.repository.LocalCityRepository
import com.karrad.ticketsclient.domain.usecase.SearchCitiesUseCase

/**
 * Manual dependency container. Holds singleton instances for the app lifecycle.
 * Replace with Koin/Dagger if the graph grows.
 *
 * Base URL: 10.0.2.2 is the Android emulator alias for localhost.
 * For device/iOS: change to the actual backend IP.
 */
object AppContainer {

    private const val BASE_URL = "http://10.0.2.2:8080"

    private val httpClient by lazy { createHttpClient() }

    val discoveryApiService: DiscoveryApiService by lazy {
        DiscoveryApiService(httpClient, BASE_URL)
    }

    val searchCitiesUseCase: SearchCitiesUseCase by lazy {
        SearchCitiesUseCase(LocalCityRepository())
    }
}

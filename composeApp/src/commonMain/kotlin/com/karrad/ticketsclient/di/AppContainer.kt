package com.karrad.ticketsclient.di

import com.karrad.ticketsclient.data.api.DiscoveryApiService
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.FakeDiscoveryApiService
import com.karrad.ticketsclient.data.api.createHttpClient
import com.karrad.ticketsclient.data.repository.LocalCityRepository
import com.karrad.ticketsclient.domain.usecase.SearchCitiesUseCase

/**
 * Manual dependency container. Call [init] once at app startup (MainActivity / MainViewController)
 * before any composable is rendered.
 *
 * BASE_URL: 10.0.2.2 = Android emulator alias for localhost.
 * Change to the real server IP when running on a physical device or iOS.
 */
object AppContainer {

    private const val BASE_URL = "http://10.0.2.2:8080"

    lateinit var discoveryService: DiscoveryService
        private set

    val searchCitiesUseCase: SearchCitiesUseCase by lazy {
        SearchCitiesUseCase(LocalCityRepository())
    }

    fun init(useMock: Boolean) {
        discoveryService = if (useMock) {
            FakeDiscoveryApiService()
        } else {
            DiscoveryApiService(createHttpClient(), BASE_URL)
        }
    }
}

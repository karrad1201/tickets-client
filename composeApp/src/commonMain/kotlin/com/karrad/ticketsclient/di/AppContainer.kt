package com.karrad.ticketsclient.di

import com.karrad.ticketsclient.data.api.AuthApiService
import com.karrad.ticketsclient.data.api.AuthService
import com.karrad.ticketsclient.data.api.DiscoveryApiService
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.FakeAuthService
import com.karrad.ticketsclient.data.api.FakeDiscoveryApiService
import com.karrad.ticketsclient.data.api.FakeScannerService
import com.karrad.ticketsclient.data.api.ScannerApiService
import com.karrad.ticketsclient.data.api.ScannerService
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

    lateinit var authService: AuthService
        private set

    lateinit var discoveryService: DiscoveryService
        private set

    lateinit var scannerService: ScannerService
        private set

    val searchCitiesUseCase: SearchCitiesUseCase by lazy {
        SearchCitiesUseCase(LocalCityRepository())
    }

    fun init(useMock: Boolean) {
        val httpClient = createHttpClient()
        authService = if (useMock) {
            FakeAuthService()
        } else {
            AuthApiService(httpClient, BASE_URL)
        }
        discoveryService = if (useMock) {
            FakeDiscoveryApiService()
        } else {
            DiscoveryApiService(httpClient, BASE_URL)
        }
        scannerService = if (useMock) {
            FakeScannerService()
        } else {
            ScannerApiService(httpClient, BASE_URL)
        }
    }
}

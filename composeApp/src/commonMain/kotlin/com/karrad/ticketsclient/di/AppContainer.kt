package com.karrad.ticketsclient.di

import com.karrad.ticketsclient.data.api.AuthApiService
import com.karrad.ticketsclient.data.api.AuthService
import com.karrad.ticketsclient.data.api.DiscoveryApiService
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.FakeAuthService
import com.karrad.ticketsclient.data.api.FakeDiscoveryApiService
import com.karrad.ticketsclient.data.api.FakeScannerService
import com.karrad.ticketsclient.data.api.EventApiService
import com.karrad.ticketsclient.data.api.EventService
import com.karrad.ticketsclient.data.api.FakeEventService
import com.karrad.ticketsclient.data.api.FakeGeoService
import com.karrad.ticketsclient.data.api.FakeOrderService
import com.karrad.ticketsclient.data.api.GeoApiService
import com.karrad.ticketsclient.data.api.GeoService
import com.karrad.ticketsclient.data.api.FakeTicketService
import com.karrad.ticketsclient.data.api.OrderApiService
import com.karrad.ticketsclient.data.api.OrderService
import com.karrad.ticketsclient.data.api.ScannerApiService
import com.karrad.ticketsclient.data.api.ScannerService
import com.karrad.ticketsclient.data.api.TicketApiService
import com.karrad.ticketsclient.data.api.TicketService
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

    var isMock: Boolean = false
        private set

    lateinit var authService: AuthService
        private set

    lateinit var discoveryService: DiscoveryService
        private set

    lateinit var scannerService: ScannerService
        private set

    lateinit var ticketService: TicketService
        private set

    lateinit var orderService: OrderService
        private set

    lateinit var eventService: EventService
        private set

    lateinit var geoService: GeoService
        private set

    val searchCitiesUseCase: SearchCitiesUseCase by lazy {
        SearchCitiesUseCase(LocalCityRepository())
    }

    fun init(useMock: Boolean) {
        isMock = useMock
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
        ticketService = if (useMock) {
            FakeTicketService()
        } else {
            TicketApiService(httpClient, BASE_URL)
        }
        orderService = if (useMock) {
            FakeOrderService()
        } else {
            OrderApiService(httpClient, BASE_URL)
        }
        eventService = if (useMock) {
            FakeEventService()
        } else {
            EventApiService(httpClient, BASE_URL)
        }
        geoService = if (useMock) {
            FakeGeoService()
        } else {
            GeoApiService(httpClient, BASE_URL)
        }
    }
}

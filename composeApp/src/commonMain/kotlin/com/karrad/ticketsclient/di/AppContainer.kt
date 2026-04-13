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
import com.karrad.ticketsclient.data.api.FakeFavoriteService
import com.karrad.ticketsclient.data.api.FakeProfileService
import com.karrad.ticketsclient.data.api.FavoriteApiService
import com.karrad.ticketsclient.data.api.FavoriteService
import com.karrad.ticketsclient.data.api.ProfileApiService
import com.karrad.ticketsclient.data.api.ProfileService
import com.karrad.ticketsclient.data.api.ScannerApiService
import com.karrad.ticketsclient.data.api.ScannerService
import com.karrad.ticketsclient.data.api.TicketApiService
import com.karrad.ticketsclient.data.api.TicketService
import com.karrad.ticketsclient.data.api.createHttpClient

/**
 * Manual dependency container. Call [init] once at app startup (MainActivity / MainViewController)
 * before any composable is rendered.
 *
 * BASE_URL: 10.0.2.2 = Android emulator alias for localhost.
 * Change to the real server IP when running on a physical device or iOS.
 */
object AppContainer {

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

    lateinit var profileService: ProfileService
        private set

    lateinit var favoriteService: FavoriteService
        private set

    lateinit var httpClient: io.ktor.client.HttpClient
        private set

    fun init(useMock: Boolean, baseUrl: String = "http://10.0.2.2:8080") {
        isMock = useMock
        val httpClient = createHttpClient()
        this.httpClient = httpClient
        authService = if (useMock) {
            FakeAuthService()
        } else {
            AuthApiService(httpClient, baseUrl)
        }
        discoveryService = if (useMock) {
            FakeDiscoveryApiService()
        } else {
            DiscoveryApiService(httpClient, baseUrl)
        }
        scannerService = if (useMock) {
            FakeScannerService()
        } else {
            ScannerApiService(httpClient, baseUrl)
        }
        ticketService = if (useMock) {
            FakeTicketService()
        } else {
            TicketApiService(httpClient, baseUrl)
        }
        orderService = if (useMock) {
            FakeOrderService()
        } else {
            OrderApiService(httpClient, baseUrl)
        }
        eventService = if (useMock) {
            FakeEventService()
        } else {
            EventApiService(httpClient, baseUrl)
        }
        geoService = if (useMock) {
            FakeGeoService()
        } else {
            GeoApiService(httpClient, baseUrl)
        }
        profileService = if (useMock) {
            FakeProfileService()
        } else {
            ProfileApiService(httpClient, baseUrl)
        }
        favoriteService = if (useMock) {
            FakeFavoriteService()
        } else {
            FavoriteApiService(httpClient, baseUrl)
        }
    }
}

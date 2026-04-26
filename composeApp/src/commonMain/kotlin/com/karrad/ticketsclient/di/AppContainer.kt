package com.karrad.ticketsclient.di

import com.karrad.ticketsclient.data.api.AuthApiService
import com.karrad.ticketsclient.data.api.AuthService
import com.karrad.ticketsclient.data.api.DiscoveryApiService
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.EventApiService
import com.karrad.ticketsclient.data.api.EventService
import com.karrad.ticketsclient.data.api.FavoriteApiService
import com.karrad.ticketsclient.data.api.FavoriteService
import com.karrad.ticketsclient.data.api.GeoApiService
import com.karrad.ticketsclient.data.api.GeoService
import com.karrad.ticketsclient.data.api.OrgMemberApiService
import com.karrad.ticketsclient.data.api.OrgMemberService
import com.karrad.ticketsclient.data.api.OrderApiService
import com.karrad.ticketsclient.data.api.OrderService
import com.karrad.ticketsclient.data.api.ProfileApiService
import com.karrad.ticketsclient.data.api.ProfileService
import com.karrad.ticketsclient.data.api.ScannerApiService
import com.karrad.ticketsclient.data.api.ScannerService
import com.karrad.ticketsclient.data.api.TicketApiService
import com.karrad.ticketsclient.data.api.TicketService
import com.karrad.ticketsclient.data.api.VenueAccessGrantApiService
import com.karrad.ticketsclient.data.api.VenueAccessGrantService
import com.karrad.ticketsclient.data.api.VenueApplicationApiService
import com.karrad.ticketsclient.data.api.VenueApplicationService
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.createHttpClient

/**
 * Manual dependency container. Call [initReal] or [initMock] once at app startup
 * before any composable is rendered.
 */
object AppContainer {

    var isMock: Boolean = false
        private set

    var baseUrl: String = ""
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

    lateinit var orgMemberService: OrgMemberService
        private set

    lateinit var venueAccessGrantService: VenueAccessGrantService
        private set

    lateinit var venueApplicationService: VenueApplicationService
        private set

    lateinit var httpClient: io.ktor.client.HttpClient
        private set

    fun init(
        isMock: Boolean,
        baseUrl: String = "",
        httpClient: io.ktor.client.HttpClient,
        authService: AuthService,
        discoveryService: DiscoveryService,
        scannerService: ScannerService,
        ticketService: TicketService,
        orderService: OrderService,
        eventService: EventService,
        geoService: GeoService,
        profileService: ProfileService,
        favoriteService: FavoriteService,
        orgMemberService: OrgMemberService,
        venueAccessGrantService: VenueAccessGrantService,
        venueApplicationService: VenueApplicationService
    ) {
        this.isMock = isMock
        this.baseUrl = baseUrl
        this.httpClient = httpClient
        this.authService = authService
        this.discoveryService = discoveryService
        this.scannerService = scannerService
        this.ticketService = ticketService
        this.orderService = orderService
        this.eventService = eventService
        this.geoService = geoService
        this.profileService = profileService
        this.favoriteService = favoriteService
        this.orgMemberService = orgMemberService
        this.venueAccessGrantService = venueAccessGrantService
        this.venueApplicationService = venueApplicationService
    }
}

fun AppContainer.initReal(baseUrl: String) {
    val client = createHttpClient { AppSession.authToken }
    init(
        isMock = false,
        baseUrl = baseUrl,
        httpClient = client,
        authService = AuthApiService(client, baseUrl),
        discoveryService = DiscoveryApiService(client, baseUrl),
        scannerService = ScannerApiService(client, baseUrl),
        ticketService = TicketApiService(client, baseUrl),
        orderService = OrderApiService(client, baseUrl),
        eventService = EventApiService(client, baseUrl),
        geoService = GeoApiService(client, baseUrl),
        profileService = ProfileApiService(client, baseUrl),
        favoriteService = FavoriteApiService(client, baseUrl),
        orgMemberService = OrgMemberApiService(client, baseUrl),
        venueAccessGrantService = VenueAccessGrantApiService(client, baseUrl),
        venueApplicationService = VenueApplicationApiService(client, baseUrl)
    )
}

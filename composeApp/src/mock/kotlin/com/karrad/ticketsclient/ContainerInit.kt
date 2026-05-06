package com.karrad.ticketsclient

import com.karrad.ticketsclient.data.api.FakeAuthService
import com.karrad.ticketsclient.data.api.FakeDiscoveryApiService
import com.karrad.ticketsclient.data.api.FakeEventService
import com.karrad.ticketsclient.data.api.FakeFavoriteService
import com.karrad.ticketsclient.data.api.FakeGeoService
import com.karrad.ticketsclient.data.api.FakeOrderService
import com.karrad.ticketsclient.data.api.FakeProfileService
import com.karrad.ticketsclient.data.api.FakeScannerService
import com.karrad.ticketsclient.data.api.FakeOrgMemberService
import com.karrad.ticketsclient.data.api.FakeTicketService
import com.karrad.ticketsclient.data.api.FakeVenueAccessGrantService
import com.karrad.ticketsclient.data.api.FakeVenueApplicationService
import com.karrad.ticketsclient.data.api.FakeVenueSpaceService
import com.karrad.ticketsclient.data.api.createHttpClient
import com.karrad.ticketsclient.di.AppContainer

@Suppress("UNUSED_PARAMETER")
fun initContainer(baseUrl: String) {
    AppContainer.init(
        isMock = true,
        httpClient = createHttpClient(),
        authService = FakeAuthService(),
        discoveryService = FakeDiscoveryApiService(),
        scannerService = FakeScannerService(),
        ticketService = FakeTicketService(),
        orderService = FakeOrderService(),
        eventService = FakeEventService(),
        geoService = FakeGeoService(),
        profileService = FakeProfileService(),
        favoriteService = FakeFavoriteService(),
        orgMemberService = FakeOrgMemberService(),
        venueAccessGrantService = FakeVenueAccessGrantService(),
        venueApplicationService = FakeVenueApplicationService(),
        venueSpaceService = FakeVenueSpaceService()
    )
}

package com.karrad.ticketsclient.ui.navigation

import cafe.adriel.voyager.core.screen.Screen

// Auth — Login flow
object LoginScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.LoginScreen()
}

// Auth — Register flow
object RegisterScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.RegisterScreen()
}

data class SmsCodeScreen(val isRegistration: Boolean = false, val phone: String = "") : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.SmsCodeScreen(
        isRegistration = isRegistration,
        phone = phone
    )
}

object NameInputScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.NameInputScreen()
}

object CitySelectionScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.CitySelectionScreen()
}

object InterestsScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.InterestsScreen()
}

// Main
object MainScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.main.MainScreen()
}

// Event
data class EventDetailScreen(val eventId: String) : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.event.EventDetailScreen(eventId)
}

// Profile edit
object EditProfileScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.profile.EditProfileScreen()
}

// Seat map
data class SeatMapScreen(val eventId: String) : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.seatmap.SeatMapScreen(eventId)
}

// Search
object SearchScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.search.SearchScreen()
}

// Profile sub-screens
object FavoritesScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.profile.FavoritesScreen()
}

object SupportScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.profile.SupportScreen()
}

object AboutScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.profile.AboutScreen()
}

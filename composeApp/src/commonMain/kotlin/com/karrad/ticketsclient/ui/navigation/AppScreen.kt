package com.karrad.ticketsclient.ui.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

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

data class NameInputScreen(val phone: String = "", val code: String = "") : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.NameInputScreen(phone, code)
}

object CitySelectionScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.auth.CitySelectionScreen()
}

// Смена города из главной ленты (pop после выбора, без перехода на Interests)
object CityPickerScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        com.karrad.ticketsclient.ui.screen.auth.CitySelectionScreen(
            onCitySelected = { navigator.pop() }
        )
    }
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

// Ticket types (events without seat map)
data class TicketTypeScreen(val eventId: String) : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.tickettype.TicketTypeScreen(eventId)
}

// Order confirm
data class OrderConfirmScreen(val eventId: String, val orderId: String, val totalPrice: Int) : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.order.OrderConfirmScreen(eventId, orderId, totalPrice)
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

// Org management (OWNER)
object OrgManagementScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.org.OrgManagementScreen()
}

// Member management (MANAGER)
object MemberManagementScreen : Screen {
    @androidx.compose.runtime.Composable
    override fun Content() = com.karrad.ticketsclient.ui.screen.org.MemberManagementScreen()
}

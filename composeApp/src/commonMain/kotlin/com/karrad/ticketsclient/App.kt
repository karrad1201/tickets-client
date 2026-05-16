package com.karrad.ticketsclient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.karrad.ticketsclient.ui.navigation.EventDetailScreen
import com.karrad.ticketsclient.ui.navigation.LoginScreen
import com.karrad.ticketsclient.ui.navigation.MainScreen
import com.karrad.ticketsclient.ui.theme.AppTheme
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun App() {
    AppTheme {
        val deepLinkEventId = AppSession.pendingDeepLinkEventId
        AppSession.pendingDeepLinkEventId = null

        val startScreen: Screen = if (AppSession.authToken != null) MainScreen else LoginScreen
        val screens = if (deepLinkEventId != null) {
            listOf(startScreen, EventDetailScreen(deepLinkEventId))
        } else {
            listOf(startScreen)
        }
        Navigator(screens = screens) { navigator ->
            LaunchedEffect(Unit) {
                AppSession.liveDeepLinkEventId.filterNotNull().collect { eventId ->
                    AppSession.liveDeepLinkEventId.value = null
                    navigator.push(EventDetailScreen(eventId))
                }
            }
            CurrentScreen()
        }
    }
}

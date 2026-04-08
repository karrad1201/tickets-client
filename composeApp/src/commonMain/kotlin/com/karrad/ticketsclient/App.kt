package com.karrad.ticketsclient

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.karrad.ticketsclient.ui.navigation.LoginScreen
import com.karrad.ticketsclient.ui.navigation.MainScreen
import com.karrad.ticketsclient.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        val startScreen = if (AppSession.authToken != null) MainScreen else LoginScreen
        Navigator(screen = startScreen)
    }
}

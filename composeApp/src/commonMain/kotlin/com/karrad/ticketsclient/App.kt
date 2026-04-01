package com.karrad.ticketsclient

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.karrad.ticketsclient.ui.navigation.LoginScreen
import com.karrad.ticketsclient.ui.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        Navigator(screen = LoginScreen)
    }
}

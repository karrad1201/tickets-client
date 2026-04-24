package com.karrad.ticketsclient

import androidx.compose.ui.window.ComposeUIViewController
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.di.initReal

fun MainViewController() = ComposeUIViewController {
    AppContainer.initReal("https://api.example.com")
    AppSession.restoreFromStore()
    App()
}
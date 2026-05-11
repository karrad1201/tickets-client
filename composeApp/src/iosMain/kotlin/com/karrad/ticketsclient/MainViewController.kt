package com.karrad.ticketsclient

import androidx.compose.ui.window.ComposeUIViewController
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.di.initReal
import com.karrad.ticketsclient.push.registerPendingIosPushToken

fun MainViewController() = ComposeUIViewController {
    AppContainer.initReal("https://api.example.com")
    AppSession.restoreFromStore()
    registerPendingIosPushToken()
    App()
}
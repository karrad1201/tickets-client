package com.karrad.ticketsclient

import androidx.compose.ui.window.ComposeUIViewController
import com.karrad.ticketsclient.di.AppContainer

fun MainViewController() = ComposeUIViewController {
    AppContainer.init(useMock = false)
    AppSession.restoreFromStore()
    App()
}
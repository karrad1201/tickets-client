package com.karrad.ticketsclient.push

import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private var pendingToken: String? = null

fun onApnsPushTokenReceived(tokenHex: String) {
    pendingToken = tokenHex
    if (AppSession.authToken == null) return
    registerPendingToken()
}

fun registerPendingIosPushToken() {
    if (AppSession.authToken == null) return
    registerPendingToken()
}

private fun registerPendingToken() {
    val token = pendingToken ?: return
    CoroutineScope(Dispatchers.Default).launch {
        runCatching {
            AppContainer.pushService.registerToken(token, "ios")
        }
    }
}

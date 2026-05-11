package com.karrad.ticketsclient.push

actual fun onAfterLogin() = registerPendingIosPushToken()

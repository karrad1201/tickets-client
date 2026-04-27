package com.karrad.ticketsclient.crash

import platform.Foundation.NSLog

actual object CrashReporter {
    actual fun log(throwable: Throwable) {
        NSLog("CrashReporter: %s — %s", throwable::class.simpleName ?: "Throwable", throwable.message ?: "")
        throwable.cause?.let { NSLog("CrashReporter caused by: %s — %s", it::class.simpleName ?: "Throwable", it.message ?: "") }
    }

    actual fun setUserId(userId: String) {
        NSLog("CrashReporter: userId=%s", userId)
    }
}

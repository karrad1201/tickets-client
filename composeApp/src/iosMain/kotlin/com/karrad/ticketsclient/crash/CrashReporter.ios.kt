package com.karrad.ticketsclient.crash

actual object CrashReporter {
    actual fun log(throwable: Throwable) = Unit
    actual fun setUserId(userId: String) = Unit
}

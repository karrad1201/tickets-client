package com.karrad.ticketsclient.crash

expect object CrashReporter {
    fun log(throwable: Throwable)
    fun setUserId(userId: String)
}

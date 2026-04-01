package com.karrad.ticketsclient

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
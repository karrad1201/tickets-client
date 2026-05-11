package com.karrad.ticketsclient.data.api

interface PushService {
    suspend fun registerToken(token: String, platform: String)
    suspend fun unregisterToken(token: String)
}

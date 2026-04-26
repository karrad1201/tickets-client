package com.karrad.ticketsclient.data.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun currentTimeMs(): Long

fun createHttpClient(tokenProvider: () -> String? = { null }): HttpClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }
    client.plugin(HttpSend).intercept { request ->
        if (request.headers["Authorization"] == null) {
            tokenProvider()?.let { token ->
                request.header("Authorization", "Bearer $token")
            }
        }
        execute(request)
    }
    return client
}

package com.karrad.ticketsclient.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

expect fun currentTimeMs(): Long

fun createHttpClient(tokenProvider: () -> String? = { null }): HttpClient {
    val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(HttpResponseValidator) {
            handleResponseExceptionWithRequest { exception, _ ->
                val response = (exception as? io.ktor.client.plugins.ResponseException)?.response
                    ?: return@handleResponseExceptionWithRequest
                val detail = try {
                    response.body<JsonObject>()["detail"]?.jsonPrimitive?.content
                } catch (_: Exception) { null }
                throw ApiException(
                    message = detail ?: "Ошибка ${response.status.value}",
                    statusCode = response.status.value
                )
            }
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

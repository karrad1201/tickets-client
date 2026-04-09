package com.karrad.ticketsclient.data.cache

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes

/**
 * Загружает байты изображения по URL с кешированием через [ImageCache].
 *
 * Использование:
 * ```kotlin
 * val bytes = CachedImageLoader.load(httpClient, url, ImageCache.AVATAR_TTL_MS)
 * ```
 *
 * Если URL пустой или загрузка не удалась — возвращает null,
 * UI должен отрисовать placeholder.
 */
object CachedImageLoader {

    suspend fun load(
        client: HttpClient,
        url: String,
        ttlMs: Long = ImageCache.AVATAR_TTL_MS
    ): ByteArray? {
        if (url.isBlank()) return null

        ImageCache.get(url, ttlMs)?.let { return it }

        return try {
            val bytes = client.get(url).readBytes()
            ImageCache.put(url, bytes)
            bytes
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Инвалидирует кеш для конкретного URL.
     * Вызывать после того как пользователь обновил аватарку.
     */
    fun invalidate(url: String) {
        ImageCache.invalidate(url)
    }
}

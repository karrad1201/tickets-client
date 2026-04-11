package com.karrad.ticketsclient.data.cache

import kotlinx.datetime.Clock

/**
 * In-memory кеш изображений с TTL.
 *
 * Хранит байты изображения по URL-ключу.
 * По умолчанию TTL = 24 часа (аватарка), для событий рекомендуется 1 час.
 *
 * Ограничения текущей реализации:
 * - Кеш живёт в памяти процесса, не переживает перезапуск.
 * - Нет ограничения на общий размер кеша (контролируется вызывающим кодом).
 *
 * Следующий шаг (issue #63): persist через expect/actual FileStore,
 * чтобы кеш переживал перезапуск.
 */
object ImageCache {

    private data class Entry(val data: ByteArray, val cachedAt: Long)

    private val store = object : LinkedHashMap<String, Entry>(MAX_SIZE + 1, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry>?): Boolean =
            size > MAX_SIZE
    }

    /** Получить байты по URL если TTL не истёк. */
    fun get(url: String, ttlMs: Long = DEFAULT_TTL_MS): ByteArray? {
        val entry = store[url] ?: return null
        val age = Clock.System.now().toEpochMilliseconds() - entry.cachedAt
        return if (age < ttlMs) entry.data else null.also { store.remove(url) }
    }

    /** Сохранить байты для URL. */
    fun put(url: String, data: ByteArray) {
        store[url] = Entry(data, Clock.System.now().toEpochMilliseconds())
    }

    /** Явная инвалидация по URL (например, после обновления аватарки). */
    fun invalidate(url: String) {
        store.remove(url)
    }

    /** Очистить весь кеш. */
    fun clear() {
        store.clear()
    }

    /** TTL аватарки пользователя: 24 часа. */
    const val AVATAR_TTL_MS = 24L * 60 * 60 * 1_000

    /** TTL изображений событий: 1 час. */
    const val EVENT_IMAGE_TTL_MS = 60L * 60 * 1_000

    private const val DEFAULT_TTL_MS = AVATAR_TTL_MS

    /** Максимальное число записей в кеше (LRU-вытеснение). */
    private const val MAX_SIZE = 200
}

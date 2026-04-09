package com.karrad.ticketsclient.data.cache

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class ImageCacheTest {

    @BeforeTest
    fun setup() {
        ImageCache.clear()
    }

    @AfterTest
    fun teardown() {
        ImageCache.clear()
    }

    @Test
    fun `get returns null when cache is empty`() {
        assertNull(ImageCache.get("https://example.com/img.png"))
    }

    @Test
    fun `put and get return same bytes`() {
        val url = "https://example.com/img.png"
        val data = byteArrayOf(1, 2, 3)
        ImageCache.put(url, data)
        assertContentEquals(data, ImageCache.get(url))
    }

    @Test
    fun `get with very short TTL returns null after TTL expires`() {
        val url = "https://example.com/img.png"
        ImageCache.put(url, byteArrayOf(42))
        // TTL = 0 ms — уже истёк
        assertNull(ImageCache.get(url, ttlMs = 0L))
    }

    @Test
    fun `invalidate removes entry`() {
        val url = "https://example.com/img.png"
        ImageCache.put(url, byteArrayOf(1))
        ImageCache.invalidate(url)
        assertNull(ImageCache.get(url))
    }

    @Test
    fun `clear removes all entries`() {
        ImageCache.put("https://a.com/1.png", byteArrayOf(1))
        ImageCache.put("https://a.com/2.png", byteArrayOf(2))
        ImageCache.clear()
        assertNull(ImageCache.get("https://a.com/1.png"))
        assertNull(ImageCache.get("https://a.com/2.png"))
    }

    @Test
    fun `AVATAR_TTL_MS is 24 hours`() {
        assert(ImageCache.AVATAR_TTL_MS == 24L * 60 * 60 * 1_000)
    }

    @Test
    fun `EVENT_IMAGE_TTL_MS is 1 hour`() {
        assert(ImageCache.EVENT_IMAGE_TTL_MS == 60L * 60 * 1_000)
    }

    @Test
    fun `expired entry is removed from store on access`() {
        val url = "https://example.com/img.png"
        ImageCache.put(url, byteArrayOf(99))
        // TTL = 0 — истёк, get должен убрать из store
        assertNull(ImageCache.get(url, ttlMs = 0L))
        // повторный get с нормальным TTL тоже null (запись удалена)
        assertNull(ImageCache.get(url, ttlMs = ImageCache.AVATAR_TTL_MS))
    }
}

package com.karrad.ticketsclient

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppSessionFavoritesTest {

    @BeforeTest
    fun setUp() {
        // Сбрасываем избранное через logout (очищает кеш, но не _favorites напрямую)
        // Вместо этого — toggle в false для всех потенциальных id
        AppSession.toggleFavorite("e-1", false)
        AppSession.toggleFavorite("e-2", false)
    }

    @Test
    fun `isFavorite returns false for new event`() {
        assertFalse(AppSession.isFavorite("e-unknown"))
    }

    @Test
    fun `toggleFavorite add makes isFavorite return true`() {
        AppSession.toggleFavorite("e-1", true)
        assertTrue(AppSession.isFavorite("e-1"))
    }

    @Test
    fun `toggleFavorite remove makes isFavorite return false`() {
        AppSession.toggleFavorite("e-1", true)
        AppSession.toggleFavorite("e-1", false)
        assertFalse(AppSession.isFavorite("e-1"))
    }

    @Test
    fun `favorites are independent per eventId`() {
        AppSession.toggleFavorite("e-1", true)
        assertFalse(AppSession.isFavorite("e-2"))
        assertTrue(AppSession.isFavorite("e-1"))
    }

    @Test
    fun `toggling same event twice add is idempotent`() {
        AppSession.toggleFavorite("e-1", true)
        AppSession.toggleFavorite("e-1", true)
        assertTrue(AppSession.isFavorite("e-1"))
    }
}

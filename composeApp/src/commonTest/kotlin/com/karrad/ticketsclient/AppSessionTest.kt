package com.karrad.ticketsclient

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppSessionTest {

    @BeforeTest
    fun setUp() {
        AppSession.logout()
    }

    @Test
    fun `login stores token userId and profile fields`() {
        AppSession.login(
            token = "tok-123",
            userId = "u-1",
            phone = "+79001234567",
            fullName = "Иван Иванов",
            role = "USER",
            avatarUrl = "https://example.com/avatar.jpg",
            interests = listOf("music", "theatre")
        )

        assertEquals("tok-123", AppSession.authToken)
        assertEquals("u-1", AppSession.userId)
        assertEquals("+79001234567", AppSession.userPhone)
        assertEquals("Иван Иванов", AppSession.userName)
        assertEquals("USER", AppSession.userRole)
        assertEquals("https://example.com/avatar.jpg", AppSession.userAvatarUrl)
        assertEquals(listOf("music", "theatre"), AppSession.userInterests)
    }

    @Test
    fun `logout clears auth token and profile`() {
        AppSession.login(
            token = "tok-abc",
            userId = "u-2",
            phone = "+79009876543",
            fullName = "Мария Петрова",
            role = "ADMIN"
        )

        AppSession.logout()

        assertNull(AppSession.authToken)
        assertNull(AppSession.userId)
        assertEquals("", AppSession.userName)
        assertEquals("", AppSession.userPhone)
        assertEquals("USER", AppSession.userRole)
        assertNull(AppSession.userAvatarUrl)
        assertEquals(emptyList(), AppSession.userInterests)
    }

    @Test
    fun `logout clears cached events`() {
        AppSession.login(token = "t", userId = "u", phone = null, fullName = "x", role = "USER")

        AppSession.logout()

        assertEquals(emptyList(), AppSession.cachedEvents)
    }

    @Test
    fun `login with null phone stores empty string`() {
        AppSession.login(token = "t", userId = "u", phone = null, fullName = "Test", role = "USER")
        assertEquals("", AppSession.userPhone)
    }

    @Test
    fun `city has default value Elista`() {
        assertEquals("Элиста", AppSession.city)
    }
}

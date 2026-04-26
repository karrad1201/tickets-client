package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.AppSession
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FakeProfileServiceTest {

    private val service = FakeProfileService()

    @BeforeTest
    fun setUp() {
        AppSession.logout()
        AppSession.login(
            token = "fake-token",
            userId = "user-42",
            phone = "+79001112233",
            fullName = "Алексей Смирнов",
            role = "USER",
            interests = listOf("jazz")
        )
    }

    @Test
    fun `updateProfile returns updated fullName`() = runTest {
        val result = service.updateProfile(
            fullName = "Новое Имя",
            interests = null
        )
        assertEquals("Новое Имя", result.fullName)
    }

    @Test
    fun `updateProfile keeps existing fullName when null`() = runTest {
        val result = service.updateProfile(
            fullName = null,
            interests = null
        )
        assertEquals("Алексей Смирнов", result.fullName)
    }

    @Test
    fun `updateProfile returns updated interests`() = runTest {
        val result = service.updateProfile(
            fullName = null,
            interests = listOf("classical", "rock")
        )
        assertEquals(listOf("classical", "rock"), result.interests)
    }

    @Test
    fun `updateProfile keeps existing interests when null`() = runTest {
        val result = service.updateProfile(
            fullName = null,
            interests = null
        )
        assertEquals(listOf("jazz"), result.interests)
    }

    @Test
    fun `updateProfile returns correct userId`() = runTest {
        val result = service.updateProfile(null, null)
        assertEquals("user-42", result.id)
    }

    @Test
    fun `uploadAvatar returns avatarUrl with correct extension`() = runTest {
        val result = service.uploadAvatar(
            imageBytes = byteArrayOf(1, 2, 3),
            extension = "png"
        )
        assertTrue(result.avatarUrl?.endsWith(".png") == true)
        assertNotNull(result.avatarUrl)
    }

    @Test
    fun `uploadAvatar returns jpg extension`() = runTest {
        val result = service.uploadAvatar(byteArrayOf(), "jpg")
        assertTrue(result.avatarUrl?.contains("avatar.jpg") == true)
    }

    @Test
    fun `uploadAvatar returns correct userId`() = runTest {
        val result = service.uploadAvatar(byteArrayOf(), "jpg")
        assertEquals("user-42", result.id)
    }
}

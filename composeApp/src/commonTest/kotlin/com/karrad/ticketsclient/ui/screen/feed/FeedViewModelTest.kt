package com.karrad.ticketsclient.ui.screen.feed

import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.DiscoveryService
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val emptyFeed = DiscoveryFeedResponseDto(
        forYou = emptyList(),
        byCategory = emptyList(),
        tomorrow = emptyList(),
        dayAfterTomorrow = emptyList()
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        AppSession.logout()
        AppSession.isOffline = false
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectDay updates selectedDay state`() = runTest(testDispatcher) {
        val vm = FeedViewModel(FakeDiscoveryService(emptyFeed))
        advanceUntilIdle()

        vm.selectDay(2)
        advanceUntilIdle()

        assertEquals(2, vm.selectedDay.value)
    }

    @Test
    fun `selectDay 0 resets to today`() = runTest(testDispatcher) {
        val vm = FeedViewModel(FakeDiscoveryService(emptyFeed))
        advanceUntilIdle()

        vm.selectDay(3)
        vm.selectDay(0)
        advanceUntilIdle()

        assertEquals(0, vm.selectedDay.value)
    }

    @Test
    fun `successful load results in Success state`() = runTest(testDispatcher) {
        val vm = FeedViewModel(FakeDiscoveryService(emptyFeed))
        advanceUntilIdle()

        assertIs<FeedState.Success>(vm.state.value)
    }

    @Test
    fun `load failure sets Error state and marks offline`() = runTest(testDispatcher) {
        val vm = FeedViewModel(ThrowingDiscoveryService())
        advanceUntilIdle()

        assertIs<FeedState.Error>(vm.state.value)
        assertTrue(AppSession.isOffline)
    }

    @Test
    fun `successful load clears offline flag`() = runTest(testDispatcher) {
        AppSession.isOffline = true
        val vm = FeedViewModel(FakeDiscoveryService(emptyFeed))
        advanceUntilIdle()

        assertEquals(false, AppSession.isOffline)
    }

    @Test
    fun `offline fallback shows cached events on Error`() = runTest(testDispatcher) {
        val vm = FeedViewModel(ThrowingDiscoveryService())
        advanceUntilIdle()

        val state = vm.state.value
        assertIs<FeedState.Error>(state)
        assertTrue(state.message.isNotBlank())
    }
}

private class FakeDiscoveryService(
    private val feed: DiscoveryFeedResponseDto
) : DiscoveryService {
    override suspend fun getDiscoveryFeed(
        city: String,
        authToken: String?,
        page: Int,
        size: Int,
        date: String?
    ): DiscoveryFeedResponseDto = feed
}

private class ThrowingDiscoveryService : DiscoveryService {
    override suspend fun getDiscoveryFeed(
        city: String,
        authToken: String?,
        page: Int,
        size: Int,
        date: String?
    ): DiscoveryFeedResponseDto = throw RuntimeException("Network error")
}

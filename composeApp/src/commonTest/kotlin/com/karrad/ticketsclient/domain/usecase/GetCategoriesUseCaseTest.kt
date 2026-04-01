package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.fake.FakeCategoryRepository
import com.karrad.ticketsclient.domain.fake.testCategory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetCategoriesUseCaseTest {

    @Test
    fun `returns all categories`() = runTest {
        val useCase = GetCategoriesUseCase(FakeCategoryRepository(listOf(testCategory)))

        val result = useCase()

        assertEquals(1, result.size)
        assertEquals(testCategory.code, result.first().code)
    }

    @Test
    fun `returns empty list when no categories`() = runTest {
        val useCase = GetCategoriesUseCase(FakeCategoryRepository())

        val result = useCase()

        assertTrue(result.isEmpty())
    }
}

package com.karrad.ticketsclient.domain.fake

import com.karrad.ticketsclient.domain.model.Category
import com.karrad.ticketsclient.domain.repository.CategoryRepository

class FakeCategoryRepository(
    private val categories: List<Category> = emptyList()
) : CategoryRepository {
    override suspend fun getAll(): List<Category> = categories
}

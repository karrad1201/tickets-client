package com.karrad.ticketsclient.domain.usecase

import com.karrad.ticketsclient.domain.model.Category
import com.karrad.ticketsclient.domain.repository.CategoryRepository

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): List<Category> =
        categoryRepository.getAll()
}

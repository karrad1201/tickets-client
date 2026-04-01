package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.Category

interface CategoryRepository {
    suspend fun getAll(): List<Category>
}

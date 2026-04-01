package com.karrad.ticketsclient.domain.repository

import com.karrad.ticketsclient.domain.model.User

interface UserRepository {
    suspend fun getCurrent(): User
    suspend fun updateCategoryPreferences(categoryIds: List<String>)
}

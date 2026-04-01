package com.karrad.ticketsclient.domain.model

data class User(
    val id: String,
    val phone: String,
    val fullName: String,
    val role: UserRole = UserRole.USER,
    val cityPreference: City? = null,
    val categoryPreferences: List<Category> = emptyList()
)

package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.UserDto

interface ProfileService {
    suspend fun updateProfile(authToken: String, fullName: String?, interests: List<String>?): UserDto
    suspend fun uploadAvatar(authToken: String, imageBytes: ByteArray, extension: String): UserDto
}

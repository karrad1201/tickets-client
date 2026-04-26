package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.UserDto

interface ProfileService {
    suspend fun updateProfile(fullName: String?, interests: List<String>?): UserDto
    suspend fun uploadAvatar(imageBytes: ByteArray, extension: String): UserDto
}

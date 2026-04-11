package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.UserDto

class FakeProfileService : ProfileService {

    override suspend fun updateProfile(
        authToken: String,
        fullName: String?,
        interests: List<String>?
    ): UserDto = UserDto(
        id = AppSession.userId ?: "fake-user",
        fullName = fullName ?: AppSession.userName,
        phone = AppSession.userPhone,
        role = "USER",
        avatarUrl = AppSession.userAvatarUrl,
        interests = interests ?: AppSession.userInterests
    )

    override suspend fun uploadAvatar(
        authToken: String,
        imageBytes: ByteArray,
        extension: String
    ): UserDto = UserDto(
        id = AppSession.userId ?: "fake-user",
        fullName = AppSession.userName,
        phone = AppSession.userPhone,
        role = "USER",
        avatarUrl = "https://fake.example.com/avatar.$extension",
        interests = AppSession.userInterests
    )
}

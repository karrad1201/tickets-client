package com.karrad.ticketsclient.data.api

import com.karrad.ticketsclient.data.api.dto.AuthResponseDto
import com.karrad.ticketsclient.data.api.dto.UserDto

/**
 * Мок-реализация авторизации для разработки без бекенда.
 *
 * Принимает любой телефон.
 * Корректный код: 1234 (вход) или любой 4-значный (регистрация).
 * Для тестирования ошибки используйте код 0000.
 */
class FakeAuthService : AuthService {

    override suspend fun sendCode(phone: String) {
        // Мок: отправка кода всегда успешна
    }

    override suspend fun login(phone: String, code: String): AuthResponseDto {
        if (code == "0000") error("Неверный код")
        return AuthResponseDto(
            accessToken = "fake-token-login",
            user = UserDto(
                id = "fake-user-id",
                phone = phone,
                fullName = "Пользователь"
            )
        )
    }

    override suspend fun register(phone: String, code: String, fullName: String): AuthResponseDto {
        if (code == "0000") error("Неверный код")
        return AuthResponseDto(
            accessToken = "fake-token-register",
            user = UserDto(
                id = "fake-user-id-new",
                phone = phone,
                fullName = fullName
            )
        )
    }

    override suspend fun logout(token: String) {
        // Мок: выход всегда успешен
    }
}

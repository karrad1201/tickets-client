# ADR-002: AppSession — in-memory хранение, без персистентности

| Поле | Значение |
|---|---|
| Статус | Принято (временно) |
| Дата | 2026-04-08 |

## Контекст

После успешного входа приложение получает JWT-токен. Нужно решить, где его хранить.

## Решение

Токен хранится в `AppSession` — Kotlin `object` (синглтон), живёт только в памяти процесса. При перезапуске приложения пользователь видит экран входа заново.

```kotlin
object AppSession {
    var authToken: String? = null
    var userId: String? = null
    // ...
    fun login(token, userId, phone, name) { … }
    fun logout() { … }
}
```

`App.kt` выбирает стартовый экран:
```kotlin
val startScreen = if (AppSession.authToken != null) MainScreen else LoginScreen
```

## Почему так

- Минимальная сложность на старте продукта.
- Нет зависимости от платформенных API (DataStore на Android, Keychain на iOS).
- Токен не хранится на диске → меньше рисков при краже устройства.

## Ограничения

- Пользователь теряет сессию при каждом перезапуске приложения — **приемлемо на MVP**.
- Нет автоматического обновления токена (refresh).

## Когда пересматривать

Когда появится требование «оставаться залогиненным между запусками» — перейти на:
- Android: `EncryptedSharedPreferences` / `DataStore`
- iOS: `Keychain`
- Общий KMP-слой: `multiplatform-settings` или `kotlinx-io`

Решение о переходе фиксировать новым ADR.

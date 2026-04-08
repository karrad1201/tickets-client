# ADR-004: expect/actual для платформенного кода

| Поле | Значение |
|---|---|
| Статус | Принято |
| Дата | 2026-04-08 |

## Контекст

Некоторые компоненты требуют платформенных API, недоступных в `commonMain`: камера (QR-сканер), системные разрешения.

## Решение

Используется механизм `expect`/`actual` Kotlin Multiplatform.

В `commonMain` объявляется контракт:
```kotlin
// commonMain
@Composable
expect fun QrScannerView(onScanned: (String) -> Unit, modifier: Modifier)
```

Каждая платформа предоставляет реализацию:
```kotlin
// androidMain — CameraX + ML Kit
@Composable
actual fun QrScannerView(onScanned: (String) -> Unit, modifier: Modifier) { … }

// iosMain — заглушка (TODO: AVFoundation)
@Composable
actual fun QrScannerView(onScanned: (String) -> Unit, modifier: Modifier) { … }
```

## Когда применять

- Доступ к камере, микрофону, геолокации
- Системные уведомления
- Биометрия / Keychain
- Любое API из Android SDK или iOS Foundation, недоступное в KMP

## Когда НЕ применять

- Сетевые запросы → Ktor работает в `commonMain`
- UI-компоненты → Compose Multiplatform покрывает большинство случаев
- Хранение настроек → рассмотреть `multiplatform-settings`

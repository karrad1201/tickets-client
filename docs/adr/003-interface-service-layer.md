# ADR-003: Interface-first сервисный слой

| Поле | Значение |
|---|---|
| Статус | Принято |
| Дата | 2026-04-08 |

## Контекст

Нужна архитектура, при которой UI-экраны одинаково работают как с реальным API, так и с фейковыми данными. При этом переключение между ними не должно затрагивать UI-код.

## Решение

Каждый домен данных описывается интерфейсом в `commonMain`:

```
data/api/
├── XxxService.kt         ← интерфейс (контракт)
├── XxxApiService.kt      ← реализация через Ktor HttpClient
└── FakeXxxService.kt     ← детерминированная заглушка
```

`AppContainer` — единственное место, где выбирается реализация:

```kotlin
fun init(useMock: Boolean) {
    xxxService = if (useMock) FakeXxxService() else XxxApiService(httpClient, BASE_URL)
}
```

UI-экраны обращаются только к `AppContainer.xxxService` и не знают о реализации.

## Текущий список сервисов

| Интерфейс | ApiService | FakeService | Статус |
|---|---|---|---|
| `AuthService` | `AuthApiService` | `FakeAuthService` | ✅ реализован |
| `DiscoveryService` | `DiscoveryApiService` | `FakeDiscoveryApiService` | ✅ реализован |
| `ScannerService` | `ScannerApiService` | `FakeScannerService` | ✅ реализован |
| `TicketService` | `TicketApiService` | `FakeTicketService` | 🔲 issue #48 |
| `OrderService` | `OrderApiService` | `FakeOrderService` | 🔲 issue #49 |
| `EventService` | `EventApiService` | `FakeEventService` | 🔲 issue #50 |
| `GeoService` | `GeoApiService` | `FakeGeoService` | 🔲 issue #51 |

## Правило

PR с новым экраном, использующим данные из сети, **обязан** включать:
1. Интерфейс `XxxService`
2. Реальную реализацию `XxxApiService`
3. Фейковую реализацию `FakeXxxService`
4. Регистрацию в `AppContainer`

## Альтернативы

- **ViewModel + Repository pattern** — правильно для больших проектов, избыточно сейчас.
- **Прямые вызовы Ktor в экранах** — не тестируемо, нет возможности подменить реализацию.

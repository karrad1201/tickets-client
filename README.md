# tickets-client

Мобильное приложение платформы по продаже билетов на мероприятия.
Kotlin Multiplatform (Android + iOS), Compose Multiplatform.

Платформа федерального уровня — не привязана к городу или региону.
Позиционирование: туризм и досуг.

## Пользователи

- **A** — люди, которые хотят найти развлечение в своём городе
- **Б** — организаторы/кураторы, которые продают билеты на свои мероприятия

## Особенности

Платформа поддерживает два типа билетов:
- с позиционированием мест (схема зала)
- без привязки к конкретному месту

## Стек

| Слой | Технология |
|---|---|
| UI | Compose Multiplatform |
| Язык | Kotlin (KMP) |
| Сеть | Ktor Client |
| Навигация | Voyager |
| Таргеты | Android, iOS |
| Сборка | Gradle (Kotlin DSL) |

## Структура проекта

```
composeApp/
├── src/
│   ├── commonMain/          # Общий код (UI, сервисный слой, модели)
│   │   └── data/api/        # XxxService + XxxApiService + FakeXxxService
│   ├── androidMain/         # Android-специфичный код (Camera, MainActivity)
│   └── iosMain/             # iOS-специфичный код
iosApp/                      # iOS точка входа (Xcode)
docs/
├── CONTRIBUTING.md          # Процесс разработки
└── adr/                     # Architectural Decision Records
```

## Сборки

Проект поддерживает два Android product flavor:

| Флейвор | Сеть | Назначение |
|---|---|---|
| `mockDebug` | ❌ не нужна | Разработка, демо, PR-ревью без поднятого бекенда |
| `prodDebug` / `prodRelease` | ✅ требуется | Реальный бекенд (`ticketsbackend`) |

```shell
# Mock (без сети)
./gradlew :composeApp:assembleMockDebug

# Prod
./gradlew :composeApp:assembleProdDebug
./gradlew :composeApp:assembleProdRelease
```

> **Правило:** каждый новый сервис обязан иметь как реальную (`XxxApiService`),
> так и фейковую (`FakeXxxService`) реализацию. Mock-сборка должна полностью
> демонстрировать весь функционал без сети. Подробнее — [ADR-001](docs/adr/001-mock-prod-build-flavors.md) и [ADR-003](docs/adr/003-interface-service-layer.md).

## Реализованный функционал

| Функция | Mock | Prod |
|---|---|---|
| Лента событий | ✅ | ✅ |
| Авторизация (SMS) | ✅ | ✅ |
| QR-сканер для организаторов | ✅ | ✅ |
| Детали события | 🔲 [#50] | 🔲 |
| Покупка билетов | 🔲 [#49] | 🔲 |
| Мои билеты | 🔲 [#48] | 🔲 |
| Поиск | 🔲 [#50] | 🔲 |
| Города из API | 🔲 [#51] | 🔲 |

## Разработка

Работа ведётся через ветки и PR, не напрямую в `master`.
Неочевидные решения фиксируются в ADR.

Подробнее — [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md).

## Бэкенд

Серверная часть — отдельный репозиторий (`ticketsbackend`, Spring Boot).

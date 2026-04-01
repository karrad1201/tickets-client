# tickets-client

Мобильное приложение платформы по продаже билетов на мероприятия.
Kotlin Multiplatform (Android + iOS), Compose Multiplatform.

Платформа федерального уровня — не привязана к городу или региону.
Позиционирование: туризм и досуг.

## Пользователи

- **A** — люди, которые хотят найти развлечение в своём городе
- **Б** — кураторы, которые продают билеты на свои мероприятия

## Особенности

Платформа поддерживает два типа билетов:
- с позиционированием мест (схема зала)
- без привязки к конкретному месту

## Стек

| Слой | Технология |
|---|---|
| UI | Compose Multiplatform |
| Язык | Kotlin (KMP) |
| Таргеты | Android, iOS |
| Сборка | Gradle (Kotlin DSL) |

## Структура проекта

```
composeApp/
├── src/
│   ├── commonMain/      # Общий код (UI, логика, модели)
│   ├── androidMain/     # Android-специфичный код
│   └── iosMain/         # iOS-специфичный код
iosApp/                  # iOS точка входа (Xcode)
docs/
├── CONTRIBUTING.md      # Процесс разработки
└── adr/                 # Architectural Decision Records
```

## Быстрый старт

**Android:**
```shell
./gradlew :composeApp:assembleDebug
```

**iOS:**
Открыть `/iosApp` в Xcode и запустить.

## Разработка

Работа ведётся через ветки и PR, не напрямую в `main`.
Неочевидные решения фиксируются в ADR.

Подробнее — [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md).

## Бэкенд

Серверная часть — отдельный репозиторий (`ticketsbackend`, Spring Boot).
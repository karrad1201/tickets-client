# Architectural Decision Records

Здесь фиксируются архитектурные решения проекта.

ADR — это короткий документ, который отвечает на вопрос:
"Почему мы сделали именно так, а не иначе?"

## Как создать ADR

1. Скопировать [000-template.md](000-template.md)
2. Присвоить следующий номер: `NNN-краткое-название.md`
3. Заполнить шаблон
4. Добавить запись в таблицу ниже
5. Закоммитить вместе с изменениями, которые это решение описывает

## Индекс

| № | Название | Статус | Дата |
|---|----------|--------|------|
| [001](001-mock-prod-build-flavors.md) | Две сборки через Gradle product flavors (mock / prod) | Принято | 2026-04-08 |
| [002](002-in-memory-session.md) | AppSession — in-memory хранение, без персистентности | Принято (временно) | 2026-04-08 |
| [003](003-interface-service-layer.md) | Interface-first сервисный слой | Принято | 2026-04-08 |
| [004](004-expect-actual-platform-code.md) | expect/actual для платформенного кода | Принято | 2026-04-08 |
| [005](005-tickets-pager-layout.md) | HorizontalPager с weight(1f) — фикс перекрытия точек навбаром | Принято | 2026-04-08 |
| [006](006-city-picker-reuse.md) | Переиспользование CitySelectionScreen через callback-параметр | Принято | 2026-04-08 |
| [007](007-favorites-local-state.md) | Избранное — in-memory Set в AppSession до готовности бэкенда | Принято | 2026-04-08 |
| [008](008-qr-fullscreen-dialog.md) | Полноэкранный QR через Dialog(usePlatformDefaultWidth=false) | Принято | 2026-04-08 |
| [009](009-offline-mode-in-memory-cache.md) | Офлайн-режим — in-memory кеш билетов в AppSession | Принято | 2026-04-09 |
| [010](010-asset-cache-ttl.md) | Кеширование ассетов с TTL (ImageCache + CachedImageLoader) | Принято | 2026-04-09 |

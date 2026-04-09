# ADR-005: Лэйаут HorizontalPager в экране «Мои билеты»

**Статус:** `accepted`
**Дата:** 2026-04-09

## Контекст

Экран `TicketsScreen` строится как `Column(fillMaxSize)`. Внутри него `TicketsPager` содержал `HorizontalPager` без явного ограничения высоты. При этом плавающий `AppBottomBar` рисуется поверх контента через `Box(fillMaxSize)` в `MainScreen`. В результате дот-индикатор страниц (`●`) выходил за пределы видимой области и визуально налезал на навигационную панель.

## Решение

- `TicketsPager` получает `modifier: Modifier = Modifier` и принимает `Modifier.weight(1f)` от родителя.
- `HorizontalPager` внутри `TicketsPager` получает `Modifier.weight(1f)` в пределах своей `Column`.
- Дот-индикатор остаётся в нижней части `TicketsPager.Column`, всегда ниже пейджера и выше `Spacer(96.dp)` родителя.
- `Spacer(96.dp)` в `TicketsScreen` обеспечивает зазор под плавающим navbar.

## Альтернативы

- **`Scaffold` с `contentPadding`** — правильнее архитектурно, но требует переработки `MainScreen` и отказа от плавающего navbar-дизайна.
- **Фиксированный `padding(bottom = 96.dp)` на `TicketsPager`** — работает, но не масштабируется: высота navbar зависит от устройства и системных insets.
- **`navigationBarsPadding()` на TicketsPager** — не учитывает высоту самого floating bar (который не является системным).

## Последствия

- Дот-индикатор всегда виден над navbar. ✓
- `HorizontalPager` с `weight(1f)` растягивается на всё доступное пространство: карточка билета верстается вверху пейджера, пустое место снизу. Приемлемо для текущего дизайна.
- При добавлении новых элементов в `TicketsScreen` между header и pager нужно учитывать, что pager всегда займёт оставшуюся высоту.

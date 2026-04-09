# ADR-006: Переиспользование CitySelectionScreen для смены города из ленты

**Статус:** `accepted`
**Дата:** 2026-04-09

## Контекст

`CitySelectionScreen` использовался только в auth-флоу: после выбора города он делал `navigator.push(InterestsScreen)`. Нужно было дать пользователю возможность менять город прямо из шапки ленты (FeedHeader).

## Решение

- `CitySelectionScreen` получает необязательный параметр `onCitySelected: ((String) -> Unit)? = null`.
  - Если `null` (auth-флоу) — поведение прежнее: `push(InterestsScreen)`.
  - Если передан — вызывается колбэк (например, `navigator.pop()`).
- Добавлен `CityPickerScreen` в `AppScreen.kt` — тонкая обёртка с `onCitySelected = { navigator.pop() }`.
- `AppSession.city` переведён на `mutableStateOf` — FeedHeader реагирует на изменение города без перезапуска `LaunchedEffect` во FeedViewModel.

## Альтернативы

- **Отдельный `CityPickerScreen` с дублированием логики** — нарушает DRY, два места для правок.
- **Shared ViewModel с StateFlow для города** — правильнее в большом приложении, избыточно сейчас.
- **BottomSheet вместо полного экрана** — лучше UX, но требует Material3 ModalBottomSheet и усложняет навигацию.

## Последствия

- Единственная реализация city picker для обоих флоу. ✓
- `AppSession.city` как `mutableStateOf` — все Composable, читающие его, автоматически реагируют на смену города. ✓
- После смены города лента **не перезагружается** — FeedViewModel не получает сигнал. Данные в ленте остаются по старому городу до перехода. Нужно решить отдельным issue или добавить `LaunchedEffect(AppSession.city)` во FeedScreen.

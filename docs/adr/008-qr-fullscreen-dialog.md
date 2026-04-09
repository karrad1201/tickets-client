# ADR-008: Fullscreen QR через Dialog, фикс размера ячеек

**Статус:** `accepted`
**Дата:** 2026-04-09

## Контекст

Два связанных дефекта:

1. **Обрезание QR**: сетка 19×6dp + 18×1.5dp gap = 141dp. При `padding(12dp)` с обеих сторон доступное пространство = 160 - 24 = 136dp. Overflow 5dp — правые/нижние ячейки обрезались `clip`.
2. **Нажатие на QR**: открывался `EventDetailScreen` (через `TicketCard.onClick`). Нужно увеличенный QR.

## Решение

### Фикс размера
- Gap уменьшен с 1.5dp до 1dp: 19×6 + 18×1 = 132dp ≤ 136dp. ✓
- `QrCode` принимает параметры `cellDp`/`gapDp` — один компонент для обычного (6/1) и fullscreen (14/2) размеров.
- `clip(RoundedCornerShape)` заменён на `background(Color.White, RoundedCornerShape)` — скругление фона без clip контента, ячейки не обрезаются.

### Fullscreen
- `QrFullscreenDialog` — Compose `Dialog` с `usePlatformDefaultWidth = false`.
- Тёмный overlay (`Color.Black.copy(alpha=0.92)`), клик по фону = закрыть.
- QR в fullscreen: cellDp=14, gapDp=2 → 302dp сетка + padding → ~334dp общий размер. Читаемо на всех телефонах.
- Кнопка ✕ в правом верхнем углу + подсказка «Нажмите для закрытия».

## Альтернативы

- **Новый экран (push) для fullscreen** — создаёт запись в back stack, нужна `@Parcelize` / `@Serializable` обёртка. Излишне для простого оверлея.
- **`BottomSheet`** — не подходит: QR нужен по центру с тёмным фоном, не снизу.
- **Увеличить контейнер до 200dp** — решает overflow, но не даёт fullscreen UX.

## Последствия

- QR отрисовывается корректно на всех размерах ячеек. ✓
- Fullscreen Dialog блокирует back stack правильно — нажатие «Назад» закрывает диалог, не экран. ✓
- `QR_PATTERN` вынесен на уровень файла — не создаётся при каждой recomposition. ✓

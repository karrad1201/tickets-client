package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Data ──────────────────────────────────────────────────────────────────────

private data class CategoryTag(val name: String, val emoji: String)

private val allCategories = listOf(
    CategoryTag("Кино",       "🎬"),
    CategoryTag("Театр",      "🎭"),
    CategoryTag("Концерты",   "🎵"),
    CategoryTag("Шоу",        "✨"),
    CategoryTag("Вечеринки",  "🎊"),
    CategoryTag("Спорт",      "⚽"),
    CategoryTag("Выставки",   "🖼️"),
    CategoryTag("Стендап",    "🎤"),
    CategoryTag("Детям",      "🎠"),
    CategoryTag("Фестивали",  "🎪"),
    CategoryTag("Лекции",     "📚"),
    CategoryTag("Танцы",      "💃"),
    CategoryTag("Кулинария",  "🍜"),
    CategoryTag("Спектакли",  "🎪"),
    CategoryTag("Ярмарки",    "🛍️"),
)

private val SHOW_COUNT = 5          // показываем первых N, остальные — "+X"
private val ageOptions = listOf("0+", "6+", "12+", "18+")
private val dateOptions = listOf("Сегодня", "Завтра", "Послезавтра")

// ─── Sheet ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedCategories = remember { mutableStateListOf<String>() }
    var selectedAge      by remember { mutableStateOf<String?>(null) }
    var selectedDate     by remember { mutableStateOf(dateOptions[0]) }
    var priceFrom        by remember { mutableStateOf("") }
    var priceTo          by remember { mutableStateOf("") }
    var showAllCategories by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 32.dp)
        ) {
            // ─── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Фильтры",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = onDismiss) {
                    Text(
                        "Закрыть",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── Категория ───────────────────────────────────────────────────
            FilterSectionTitle("Категория")
            Spacer(Modifier.height(10.dp))
            CategoryChips(
                categories = allCategories,
                selected = selectedCategories,
                showAll = showAllCategories,
                visibleCount = SHOW_COUNT,
                onToggle = { name ->
                    if (name in selectedCategories) selectedCategories.remove(name)
                    else selectedCategories.add(name)
                },
                onShowAll = { showAllCategories = true }
            )

            Spacer(Modifier.height(20.dp))

            // ─── Возраст ─────────────────────────────────────────────────────
            FilterSectionTitle("Возраст")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ageOptions.forEach { age ->
                    SimpleChip(
                        label = age,
                        selected = selectedAge == age,
                        onClick = { selectedAge = if (selectedAge == age) null else age }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── Дата ────────────────────────────────────────────────────────
            FilterSectionTitle("Выбрать дату")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                dateOptions.forEach { date ->
                    val isToday = date == dateOptions[0]
                    DateChip(
                        label = date,
                        selected = selectedDate == date,
                        showIcon = isToday,
                        onClick = { selectedDate = date }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ─── Цена ────────────────────────────────────────────────────────
            FilterSectionTitle("Цена")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PriceField(
                    value = priceFrom,
                    onValueChange = { priceFrom = it },
                    placeholder = "от",
                    modifier = Modifier.weight(1f),
                    hasValue = priceFrom.isNotEmpty()
                )
                PriceField(
                    value = priceTo,
                    onValueChange = { priceTo = it },
                    placeholder = "до",
                    modifier = Modifier.weight(1f),
                    hasValue = priceTo.isNotEmpty()
                )
            }

            Spacer(Modifier.height(28.dp))

            // ─── Кнопки ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Сбросить
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            selectedCategories.clear()
                            selectedAge = null
                            selectedDate = dateOptions[0]
                            priceFrom = ""
                            priceTo = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Сбросить",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                // Применить
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Применить",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Категория — чипы с эмодзи ─────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryChips(
    categories: List<CategoryTag>,
    selected: List<String>,
    showAll: Boolean,
    visibleCount: Int,
    onToggle: (String) -> Unit,
    onShowAll: () -> Unit
) {
    val visible = if (showAll) categories else categories.take(visibleCount)
    val remaining = categories.size - visibleCount

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        visible.forEach { tag ->
            val isSelected = tag.name in selected
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onToggle(tag.name) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(tag.emoji, fontSize = 14.sp)
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (!showAll && remaining > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onShowAll() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "+$remaining",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── Простой чип (Возраст) ─────────────────────────────────────────────────────

@Composable
private fun SimpleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}

// ─── Чип даты ──────────────────────────────────────────────────────────────────

@Composable
private fun DateChip(label: String, selected: Boolean, showIcon: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (showIcon) {
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = null,
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}

// ─── Поле цены ─────────────────────────────────────────────────────────────────

@Composable
private fun PriceField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    hasValue: Boolean
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                inner()
            },
            modifier = Modifier.weight(1f)
        )
        Text(
            "₽",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (hasValue) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Заголовок секции ──────────────────────────────────────────────────────────

@Composable
private fun FilterSectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
    )
}

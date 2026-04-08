package com.karrad.ticketsclient.ui.screen.event

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.OrderConfirmScreen
import com.karrad.ticketsclient.ui.screen.feed.EventImagePlaceholder
import com.karrad.ticketsclient.ui.util.formatPrice
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun EventDetailScreen(eventId: String) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    var event by remember { mutableStateOf(AppSession.currentEvent?.takeIf { it.id == eventId }) }
    var buyLoading by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        if (event == null) {
            event = try { AppContainer.eventService.getEvent(eventId) } catch (_: Exception) { null }
        }
    }

    if (event == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    @Suppress("NAME_SHADOWING") val event = event!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ─── Hero ────────────────────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                EventImagePlaceholder(seed = event.id, modifier = Modifier.fillMaxSize())

                // тёмный градиент снизу
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                startY = 100f
                            )
                        )
                )

                // Кнопка назад
                Box(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ─── Контент ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // Теги: возраст + площадка
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    event.ageRating?.let { TagChip(text = it, outlined = true) }
                    TagChip(text = event.venueId.venueShort(), filled = true)
                }

                Spacer(Modifier.height(12.dp))

                // Название
                Text(
                    text = event.label,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                // Дата
                event.time.formatEventDate()?.let { dateStr ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                // О мероприятии
                Text(
                    "О мероприятии",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                ExpandableText(text = event.description)

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                // Место проведения
                Text(
                    "Место проведения",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = event.venueId.venueFull(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                // Дата мероприятия
                Text(
                    "Дата мероприятия",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = event.time.formatEventDateFull() ?: event.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(100.dp))
            }
        }

        // ─── Sticky CTA ───────────────────────────────────────────────────────
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Кнопка во всю ширину, цена внутри справа
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (buyLoading) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.primary
                        )
                        .clickable(enabled = !buyLoading) {
                            buyLoading = true
                            scope.launch {
                                try {
                                    val order = AppContainer.orderService.createOrder(
                                        eventId = event.id,
                                        authToken = AppSession.authToken ?: ""
                                    )
                                    navigator.push(
                                        OrderConfirmScreen(
                                            eventId = event.id,
                                            orderId = order.id,
                                            totalPrice = order.totalPrice
                                        )
                                    )
                                } catch (_: Exception) {
                                    // оставляем на экране, кнопка снова активна
                                } finally {
                                    buyLoading = false
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (buyLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Купить билет",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            event.minPrice?.let { price ->
                                Text(
                                    "от ${price.formatPrice()} ₽",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    text: String,
    outlined: Boolean = false,
    filled: Boolean = false
) {
    val bg = when {
        filled   -> MaterialTheme.colorScheme.primary
        outlined -> Color.Transparent
        else     -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        filled   -> Color.White
        outlined -> MaterialTheme.colorScheme.onSurfaceVariant
        else     -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderMod = if (outlined)
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    else
        Modifier.clip(RoundedCornerShape(20.dp)).background(bg)

    Box(
        modifier = borderMod.padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = textColor)
    }
}

@Composable
private fun ExpandableText(text: String, collapsedLines: Int = 4) {
    var expanded by remember { mutableStateOf(false) }
    val isLong = text.length > 180

    Column(Modifier.animateContentSize()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedLines,
            overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
        )
        if (isLong) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (expanded) "Свернуть" else "Читать полностью",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

private fun String.formatEventDate(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек")
    "${ldt.dayOfMonth} ${months[ldt.monthNumber - 1]} в ${ldt.hour.toString().padStart(2,'0')}:${ldt.minute.toString().padStart(2,'0')}"
}.getOrNull()

private fun String.formatEventDateFull(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    val monthsFull = listOf("января","февраля","марта","апреля","мая","июня",
        "июля","августа","сентября","октября","ноября","декабря")
    "${ldt.dayOfMonth} ${monthsFull[ldt.monthNumber - 1]} ${ldt.year}, " +
        "${ldt.hour.toString().padStart(2,'0')}:${ldt.minute.toString().padStart(2,'0')}"
}.getOrNull()

private fun String.venueShort(): String = when (this) {
    "venue-bolshoi"  -> "Большой театр"
    "venue-arena"    -> "Арена"
    "venue-cinema"   -> "Кинотеатр Октябрь"
    "venue-club"     -> "Известия Hall"
    "venue-museum"   -> "Музей совр. искусства"
    "venue-theater"  -> "Театр на Таганке"
    else             -> this
}

private fun String.venueFull(): String = when (this) {
    "venue-bolshoi"  -> "Большой театр, ул. Петровка, 1"
    "venue-arena"    -> "Спортивный комплекс Арена, Лужнецкая наб., 24"
    "venue-cinema"   -> "Кинотеатр Октябрь, Новый Арбат, 24"
    "venue-club"     -> "Известия Hall, Пушкинская площадь, 5"
    "venue-museum"   -> "ГМИИ им. Пушкина, ул. Волхонка, 12"
    "venue-theater"  -> "Театр на Таганке, Земляной вал, 76"
    else             -> this
}

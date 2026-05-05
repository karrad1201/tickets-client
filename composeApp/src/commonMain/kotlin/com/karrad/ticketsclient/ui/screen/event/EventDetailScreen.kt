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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.rememberCoroutineScope
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.SeatMapScreen
import com.karrad.ticketsclient.ui.navigation.TicketTypeScreen
import com.karrad.ticketsclient.ui.component.EventImage
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
    var loadedEvent by remember { mutableStateOf<EventDto?>(null) }
    var isFavorite by remember { mutableStateOf(AppSession.isFavorite(eventId)) }
    val favoriteColor by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFE53935) else Color.Black,
        animationSpec = tween(200),
        label = "favColor"
    )

    LaunchedEffect(eventId) {
        loadedEvent = try { AppContainer.eventService.getEvent(eventId) } catch (e: Exception) { CrashReporter.log(e); null }
    }

    val event = loadedEvent ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

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
                EventImage(imageUrl = event.imageUrl, seed = event.id, modifier = Modifier.fillMaxSize())

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

                // Кнопки: назад (слева) + избранное (справа)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f)),
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

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                            .clickable {
                                val newFavorite = !isFavorite
                                isFavorite = newFavorite
                                AppSession.toggleFavorite(eventId, newFavorite)
                                scope.launch {
                                    runCatching {
                                        if (newFavorite) {
                                            AppContainer.favoriteService.add(eventId)
                                        } else {
                                            AppContainer.favoriteService.remove(eventId)
                                        }
                                    }.onFailure {
                                        CrashReporter.log(it)
                                        isFavorite = !newFavorite
                                        AppSession.toggleFavorite(eventId, !newFavorite)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                            tint = favoriteColor,
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
                    TagChip(text = event.venueLabel ?: event.venueId, filled = true)
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
                        text = event.venueLabel ?: event.venueId,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if (event.hasSeatMap) {
                            navigator.push(SeatMapScreen(event.id))
                        } else {
                            navigator.push(TicketTypeScreen(event.id))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Выбрать",
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


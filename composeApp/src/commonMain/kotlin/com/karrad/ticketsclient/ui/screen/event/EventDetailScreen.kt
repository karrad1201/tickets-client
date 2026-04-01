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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.ui.navigation.SeatMapScreen
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun EventDetailScreen(eventId: String) {
    val navigator = LocalNavigator.currentOrThrow
    val event = AppSession.currentEvent

    if (event == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Событие не найдено")
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HeroImage(event = event, onBack = { navigator.pop() })

            Column(Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Text(
                    text = event.label,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(12.dp))

                event.time.formatEventDate()?.let { dateStr ->
                    TagChip(
                        icon = { Icon(Icons.Default.DateRange, null, Modifier.size(16.dp)) },
                        text = dateStr
                    )
                    Spacer(Modifier.height(8.dp))
                }

                TagChip(
                    icon = { Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp)) },
                    text = event.venueId.venueLabel()
                )

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(20.dp))

                Text(
                    "О мероприятии",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                ExpandableText(event.description)

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(20.dp))

                Text(
                    "Место проведения",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = event.venueId.venueLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(20.dp))

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

                // Space for sticky button
                Spacer(Modifier.height(100.dp))
            }
        }

        // Sticky bottom CTA
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                event.minPrice?.let { price ->
                    Text(
                        text = "от ${price.formatPrice()} ₽",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                ElevatedButton(
                    onClick = { navigator.push(SeatMapScreen(event.id)) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Выбрать места", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun HeroImage(event: EventDto, onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        HeroPlaceholder(event.id, Modifier.fillMaxSize())

        // Bottom gradient overlay
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        startY = 120f
                    )
                )
        )

        // Back button
        Box(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }
        }

        // Price badge top-right
        event.minPrice?.let { price ->
            Box(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(12.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "от ${price.formatPrice()} ₽",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun HeroPlaceholder(id: String, modifier: Modifier = Modifier) {
    val palettes = listOf(
        listOf(Color(0xFF1A1A2E), Color(0xFF16213E)),
        listOf(Color(0xFF0F3460), Color(0xFF533483)),
        listOf(Color(0xFF2D6A4F), Color(0xFF1B4332)),
        listOf(Color(0xFF6A0572), Color(0xFF3A0CA3)),
        listOf(Color(0xFF7B2D00), Color(0xFF3E1200)),
        listOf(Color(0xFF023E8A), Color(0xFF0077B6))
    )
    val palette = palettes[kotlin.math.abs(id.hashCode()) % palettes.size]
    Box(modifier = modifier.background(Brush.verticalGradient(palette)))
}

@Composable
private fun TagChip(icon: @Composable () -> Unit, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ExpandableText(text: String, collapsedMaxLines: Int = 4) {
    var expanded by remember { mutableStateOf(false) }
    val isLong = text.length > 200

    Column(Modifier.animateContentSize()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
        )
        if (isLong) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (expanded) "Свернуть" else "Читать далее",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

private fun String.formatEventDate(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")
    "${ldt.dayOfMonth} ${months[ldt.monthNumber - 1]}, ${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
}.getOrNull()

private fun String.formatEventDateFull(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    val monthsFull = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )
    "${ldt.dayOfMonth} ${monthsFull[ldt.monthNumber - 1]} ${ldt.year}, " +
        "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
}.getOrNull()

private fun Int.formatPrice(): String {
    val s = this.toString()
    return if (s.length <= 3) s else s.dropLast(3) + "\u00A0" + s.takeLast(3)
}

private fun String.venueLabel(): String = when (this) {
    "venue-bolshoi"  -> "Большой театр"
    "venue-arena"    -> "Спортивный комплекс Арена"
    "venue-cinema"   -> "Кинотеатр Октябрь"
    "venue-club"     -> "Клуб Известия Hall"
    "venue-museum"   -> "Музей современного искусства"
    "venue-theater"  -> "Театр на Таганке"
    else             -> this
}

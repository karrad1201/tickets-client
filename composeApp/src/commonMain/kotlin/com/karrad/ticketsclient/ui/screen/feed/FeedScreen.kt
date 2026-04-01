package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.CategoryEventsEntryDto
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun FeedScreen() {
    val viewModel = viewModel { FeedViewModel(AppContainer.discoveryService) }
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        FeedHeader()

        when (val s = state) {
            is FeedState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is FeedState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Не удалось загрузить события",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.load() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is FeedState.Success -> {
                FeedContent(feed = s.feed, onEventClick = {})
            }
        }
    }
}

@Composable
private fun FeedHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = AppSession.city,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Поиск",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = "Фильтры",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FeedContent(
    feed: DiscoveryFeedResponseDto,
    onEventClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (feed.forYou.isNotEmpty()) {
            item {
                SectionHeader(title = "Для вас")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(feed.forYou, key = { it.id }) { event ->
                        LargeEventCard(event = event, onClick = { onEventClick(event.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (feed.tomorrow.isNotEmpty()) {
            item {
                SectionHeader(title = "Завтра")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(feed.tomorrow, key = { it.id }) { event ->
                        SmallEventCard(event = event, onClick = { onEventClick(event.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (feed.dayAfterTomorrow.isNotEmpty()) {
            item {
                SectionHeader(title = "Послезавтра")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(feed.dayAfterTomorrow, key = { it.id }) { event ->
                        SmallEventCard(event = event, onClick = { onEventClick(event.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        items(feed.byCategory, key = { it.category.id }) { entry ->
            CategorySection(entry = entry, onEventClick = onEventClick)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, onMoreClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (onMoreClick != null) {
            Text(
                text = "Ещё",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onMoreClick() }
            )
        }
    }
}

@Composable
private fun CategorySection(
    entry: CategoryEventsEntryDto,
    onEventClick: (String) -> Unit
) {
    SectionHeader(title = entry.category.label, onMoreClick = {})
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(entry.events, key = { it.id }) { event ->
            SmallEventCard(event = event, onClick = { onEventClick(event.id) })
        }
    }
}

@Composable
private fun LargeEventCard(event: EventDto, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box {
            EventImagePlaceholder(
                seed = event.id,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                            startY = 80f
                        )
                    )
            )

            event.minPrice?.let { price ->
                PriceBadge(
                    price = price,
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = event.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                EventDateChip(isoInstant = event.time, onDark = true)
            }
        }
    }
}

@Composable
private fun SmallEventCard(event: EventDto, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            EventImagePlaceholder(
                seed = event.id,
                modifier = Modifier.fillMaxSize()
            )
            event.minPrice?.let { price ->
                PriceBadge(
                    price = price,
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = event.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        EventDateChip(isoInstant = event.time, onDark = false)
    }
}

@Composable
private fun PriceBadge(price: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = "от ${formatPrice(price)} ₽",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun EventDateChip(isoInstant: String, onDark: Boolean) {
    val formatted = remember(isoInstant) { formatEventDate(isoInstant) }
    val tint = if (onDark) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = formatted,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}

@Composable
private fun EventImagePlaceholder(seed: String, modifier: Modifier = Modifier) {
    val palettes = listOf(
        listOf(Color(0xFF6B4EFF), Color(0xFF9B7BFF)),
        listOf(Color(0xFF1A73E8), Color(0xFF4EAAFF)),
        listOf(Color(0xFFE84040), Color(0xFFFF7B7B)),
        listOf(Color(0xFF00A86B), Color(0xFF4ECBA0)),
        listOf(Color(0xFFFF6B2B), Color(0xFFFFAA7B)),
        listOf(Color(0xFF8B2FC9), Color(0xFFCB7BFF)),
    )
    val idx = (seed.hashCode() and 0x7FFFFFFF) % palettes.size
    val (start, end) = palettes[idx]
    Box(
        modifier = modifier.background(Brush.linearGradient(colors = listOf(start, end)))
    )
}

private fun formatPrice(price: Int): String {
    return if (price >= 1000) {
        "${price / 1000}\u00A0${(price % 1000).toString().padStart(3, '0')}"
    } else {
        price.toString()
    }
}

private val MONTH_SHORT = listOf(
    "янв", "фев", "мар", "апр", "мая", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)

private fun formatEventDate(isoInstant: String): String {
    return try {
        val instant = Instant.parse(isoInstant)
        val dt = instant.toLocalDateTime(TimeZone.of("Europe/Moscow"))
        val month = MONTH_SHORT[dt.monthNumber - 1]
        val hour = dt.hour.toString().padStart(2, '0')
        val minute = dt.minute.toString().padStart(2, '0')
        "${dt.dayOfMonth} $month в $hour:$minute"
    } catch (_: Exception) {
        isoInstant
    }
}

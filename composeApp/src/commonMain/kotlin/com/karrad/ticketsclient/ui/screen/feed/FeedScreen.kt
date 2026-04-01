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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.CategoryEventsEntryDto
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun FeedScreen() {
    val viewModel = viewModel { FeedViewModel(AppContainer.discoveryService) }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
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
            is FeedState.Success -> FeedContent(feed = s.feed)
        }
    }
}

// ─── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun FeedHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
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

// ─── Date strip ────────────────────────────────────────────────────────────────

@Composable
private fun DateStrip() {
    val tz = TimeZone.currentSystemDefault()
    val today = remember { Clock.System.now().toLocalDateTime(tz).date }
    var selectedIndex by remember { mutableStateOf(0) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(14) { i ->
                val date = today.plus(i, DateTimeUnit.DAY)
                DateCell(
                    dayNumber = date.dayOfMonth,
                    weekday = date.dayOfWeek.shortRu(),
                    selected = selectedIndex == i,
                    onClick = { selectedIndex = i }
                )
            }
        }
    }
}

@Composable
private fun DateCell(dayNumber: Int, weekday: String, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val subColor = if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                   else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayNumber.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = weekday,
                style = MaterialTheme.typography.labelSmall,
                color = subColor
            )
        }
    }
}

private fun DayOfWeek.shortRu(): String = when (this) {
    DayOfWeek.MONDAY -> "ПН"
    DayOfWeek.TUESDAY -> "ВТ"
    DayOfWeek.WEDNESDAY -> "СР"
    DayOfWeek.THURSDAY -> "ЧТ"
    DayOfWeek.FRIDAY -> "ПТ"
    DayOfWeek.SATURDAY -> "СБ"
    DayOfWeek.SUNDAY -> "ВС"
    else -> ""
}

// ─── Feed content ──────────────────────────────────────────────────────────────

@Composable
private fun FeedContent(feed: DiscoveryFeedResponseDto) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        stickyHeader {
            DateStrip()
        }

        if (feed.forYou.isNotEmpty()) {
            item { ForYouSection(events = feed.forYou) }
        }

        if (feed.tomorrow.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalEventSection(title = "Завтра", events = feed.tomorrow)
            }
        }

        if (feed.dayAfterTomorrow.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalEventSection(title = "Послезавтра", events = feed.dayAfterTomorrow)
            }
        }

        items(feed.byCategory, key = { it.category.id }) { entry ->
            Spacer(modifier = Modifier.height(8.dp))
            CategorySection(entry = entry)
        }
    }
}

// ─── "Для вас" pager ───────────────────────────────────────────────────────────

@Composable
private fun ForYouSection(events: List<EventDto>) {
    val pagerState = rememberPagerState { events.size }

    Column {
        SectionHeader(title = "Для вас")

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            LargeEventCard(event = events[page])
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(events.size) { i ->
                val isSelected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

@Composable
private fun LargeEventCard(event: EventDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Gradient background image placeholder
        EventImagePlaceholder(seed = event.id, modifier = Modifier.fillMaxSize())

        // Bottom gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.75f)
                    )
                )
        )

        // Price badge — top right
        event.minPrice?.let {
            PriceBadge(
                price = it,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            )
        }

        // Text — bottom left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
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
            EventMeta(isoInstant = event.time, onDark = true)
        }
    }
}

// ─── Generic horizontal section (Завтра / Послезавтра) ─────────────────────────

@Composable
private fun HorizontalEventSection(title: String, events: List<EventDto>) {
    Column {
        SectionHeader(title = title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events, key = { it.id }) { event ->
                SmallEventCard(event = event)
            }
        }
    }
}

// ─── Category section ──────────────────────────────────────────────────────────

@Composable
private fun CategorySection(entry: CategoryEventsEntryDto) {
    Column {
        SectionHeader(title = entry.category.label, hasMore = true)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(entry.events, key = { it.id }) { event ->
                SmallEventCard(event = event)
            }
        }
    }
}

// ─── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, hasMore: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp, top = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (hasMore) {
            Text(
                text = "Ещё",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Small event card (portrait) ───────────────────────────────────────────────

@Composable
private fun SmallEventCard(event: EventDto) {
    Card(
        modifier = Modifier.width(152.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image area with price badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                EventImagePlaceholder(seed = event.id, modifier = Modifier.fillMaxSize())
                event.minPrice?.let {
                    PriceBadge(
                        price = it,
                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                    )
                }
            }

            // Text area
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = event.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.3
                )
                Spacer(modifier = Modifier.height(4.dp))
                EventMeta(isoInstant = event.time, onDark = false)
            }
        }
    }
}

// ─── Shared components ─────────────────────────────────────────────────────────

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
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EventMeta(isoInstant: String, onDark: Boolean) {
    val date = remember(isoInstant) { formatEventDate(isoInstant) }
    val tint = if (onDark) Color.White.copy(alpha = 0.8f)
               else MaterialTheme.colorScheme.onSurfaceVariant
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            maxLines = 1
        )
    }
}

@Composable
private fun EventImagePlaceholder(seed: String, modifier: Modifier = Modifier) {
    val palettes = listOf(
        listOf(Color(0xFF5C3FCC), Color(0xFF8B6FE8)),
        listOf(Color(0xFF1557B0), Color(0xFF3A8FE8)),
        listOf(Color(0xFFC0392B), Color(0xFFE74C3C)),
        listOf(Color(0xFF1A7A4A), Color(0xFF27AE60)),
        listOf(Color(0xFFD35400), Color(0xFFE67E22)),
        listOf(Color(0xFF6C3483), Color(0xFF9B59B6)),
        listOf(Color(0xFF1A5276), Color(0xFF2E86C1)),
        listOf(Color(0xFF78281F), Color(0xFFC0392B)),
    )
    val idx = (seed.hashCode() and 0x7FFFFFFF) % palettes.size
    val (start, end) = palettes[idx]
    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(start, end))
        )
    )
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

private fun formatPrice(price: Int): String = when {
    price >= 1000 -> "${price / 1000}\u00A0${(price % 1000).toString().padStart(3, '0')}"
    else -> price.toString()
}

private val MONTHS = listOf(
    "янв", "фев", "мар", "апр", "мая", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)

private fun formatEventDate(iso: String): String = try {
    val dt = Instant.parse(iso).toLocalDateTime(TimeZone.of("Europe/Moscow"))
    val h = dt.hour.toString().padStart(2, '0')
    val m = dt.minute.toString().padStart(2, '0')
    "${dt.dayOfMonth} ${MONTHS[dt.monthNumber - 1]} в $h:$m"
} catch (_: Exception) { iso }

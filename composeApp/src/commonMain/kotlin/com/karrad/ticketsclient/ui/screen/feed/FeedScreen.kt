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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.CategoryEventsEntryDto
import com.karrad.ticketsclient.data.api.dto.DiscoveryFeedResponseDto
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.EventDetailScreen
import com.karrad.ticketsclient.ui.util.formatPrice
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

// ─── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun FeedScreen() {
    // Берём родительский навигатор: EventDetailScreen должен открываться
    // поверх TabNavigator (без нижней панели)
    val navigator = LocalNavigator.currentOrThrow
    val rootNavigator = navigator.parent ?: navigator

    val viewModel = viewModel { FeedViewModel(AppContainer.discoveryService) }
    val state by viewModel.state.collectAsState()
    var showFilters by rememberSaveable { mutableStateOf(false) }

    if (showFilters) {
        FiltersBottomSheet(onDismiss = { showFilters = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        FeedHeader(onFilterClick = { showFilters = true })

        when (val s = state) {
            is FeedState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is FeedState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Не удалось загрузить события",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.load() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Повторить") }
                }
            }
            is FeedState.Success -> {
                FeedContent(feed = s.feed, onEventClick = { event ->
                    AppSession.currentEvent = event
                    rootNavigator.push(EventDetailScreen(event.id))
                })
            }
        }
    }
}

// ─── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun FeedHeader(onFilterClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = AppSession.city,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Поиск",
                tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Outlined.FilterList, contentDescription = "Фильтры",
                tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
        }
    }
}

// ─── Date strip ────────────────────────────────────────────────────────────────

@Composable
private fun DateStrip(selectedDay: Int, onDaySelect: (Int) -> Unit) {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(14) { offset ->
            val date = today.plus(offset, DateTimeUnit.DAY)
            val isSelected = offset == selectedDay
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDaySelect(offset) }
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = date.dayOfWeek.shortRu(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun DayOfWeek.shortRu(): String = when (this) {
    DayOfWeek.MONDAY    -> "ПН"
    DayOfWeek.TUESDAY   -> "ВТ"
    DayOfWeek.WEDNESDAY -> "СР"
    DayOfWeek.THURSDAY  -> "ЧТ"
    DayOfWeek.FRIDAY    -> "ПТ"
    DayOfWeek.SATURDAY  -> "СБ"
    DayOfWeek.SUNDAY    -> "ВС"
}

// ─── Feed content ──────────────────────────────────────────────────────────────

@Composable
private fun FeedContent(feed: DiscoveryFeedResponseDto, onEventClick: (EventDto) -> Unit) {
    var selectedDay by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp) // место под плавающий нав-бар
    ) {
        stickyHeader {
            DateStrip(selectedDay = selectedDay, onDaySelect = { selectedDay = it })
        }

        if (feed.forYou.isNotEmpty()) {
            item {
                SectionHeader("Для вас")
                ForYouSection(events = feed.forYou, onEventClick = onEventClick)
            }
        }

        feed.byCategory.forEach { entry ->
            item {
                SectionHeader(entry.category.label, hasMore = true)
                HorizontalEventRow(events = entry.events, onEventClick = onEventClick)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        if (hasMore) {
            Text(
                ">",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── "Для вас" row ─────────────────────────────────────────────────────────────

@Composable
private fun ForYouSection(events: List<EventDto>, onEventClick: (EventDto) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        items(events, key = { it.id }) { event ->
            PortraitEventCard(event = event, onClick = { onEventClick(event) })
        }
    }
}

// ─── Portrait event card (универсальная, "Для вас" + категории) ────────────────

@Composable
private fun PortraitEventCard(event: EventDto, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        EventImagePlaceholder(seed = event.id, modifier = Modifier.fillMaxSize())

        // Gradient overlay — нижняя треть
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f)),
                    startY = 200f
                )
            )
        )

        // Price badge — top right
        event.minPrice?.let { price ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "от ${price.formatPrice()} ₽",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Title + venue — bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = event.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = event.venueId.venueShort(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Horizontal category row ───────────────────────────────────────────────────

@Composable
private fun HorizontalEventRow(events: List<EventDto>, onEventClick: (EventDto) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        items(events, key = { it.id }) { event ->
            PortraitEventCard(event = event, onClick = { onEventClick(event) })
        }
    }
}

// ─── Image placeholder ─────────────────────────────────────────────────────────

@Composable
fun EventImagePlaceholder(seed: String, modifier: Modifier = Modifier) {
    val palettes = listOf(
        listOf(Color(0xFF1A1A2E), Color(0xFF16213E)),
        listOf(Color(0xFF0F3460), Color(0xFF533483)),
        listOf(Color(0xFF2D6A4F), Color(0xFF1B4332)),
        listOf(Color(0xFF6A0572), Color(0xFF3A0CA3)),
        listOf(Color(0xFF7B2D00), Color(0xFF3E1200)),
        listOf(Color(0xFF023E8A), Color(0xFF0077B6)),
        listOf(Color(0xFF1D3557), Color(0xFF457B9D)),
        listOf(Color(0xFF3D0000), Color(0xFF6B0000))
    )
    val palette = palettes[kotlin.math.abs(seed.hashCode()) % palettes.size]
    Box(modifier = modifier.background(Brush.verticalGradient(palette)))
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

private fun String.venueShort(): String = when (this) {
    "venue-bolshoi"  -> "Большой театр"
    "venue-arena"    -> "Арена"
    "venue-cinema"   -> "Кинотеатр Октябрь"
    "venue-club"     -> "Известия Hall"
    "venue-museum"   -> "Музей совр. искусства"
    "venue-theater"  -> "Театр на Таганке"
    else             -> this
}

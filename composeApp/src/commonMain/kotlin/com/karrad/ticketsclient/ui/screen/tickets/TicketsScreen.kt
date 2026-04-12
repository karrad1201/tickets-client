package com.karrad.ticketsclient.ui.screen.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.TicketDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.EventDetailScreen

@Composable
fun TicketsScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val rootNavigator = navigator.parent ?: navigator
    var tickets by remember { mutableStateOf<List<TicketDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isFromCache by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val loaded = AppContainer.ticketService.getMyTickets(AppSession.authToken ?: "")
            tickets = loaded
            AppSession.cachedTickets = loaded   // обновляем кеш при успехе
            AppSession.isOffline = false
            isFromCache = false
        } catch (_: Exception) {
            // Нет сети — показываем кешированные билеты
            if (AppSession.cachedTickets.isNotEmpty()) {
                tickets = AppSession.cachedTickets
                isFromCache = true
            } else {
                tickets = emptyList()
                isFromCache = false
            }
            AppSession.isOffline = true
        } finally {
            loading = false
        }
    }

    val upcoming = tickets.filter { it.usedAt == null }
    val archived = tickets.filter { it.usedAt != null }
    val current = if (selectedTab == 0) upcoming else archived

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        Text(
            "Мои билеты",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )

        // ─── Offline banner ───────────────────────────────────────────────────
        if (isFromCache) {
            OfflineBanner()
        }

        // ─── Pill tabs ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            listOf("Актуальные", "Архивные").forEachIndexed { index, label ->
                val selected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { selectedTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (current.isEmpty()) {
            EmptyTickets(selectedTab == 0)
        } else {
            TicketsPager(
                tickets = current,
                isArchived = selectedTab == 1,
                modifier = Modifier.weight(1f),
                onTicketClick = { ticket ->
                    val event = AppSession.cachedEvents.find { it.label == ticket.eventLabel }
                        ?: EventDto(
                            id = ticket.eventId,
                            label = ticket.eventLabel,
                            description = "",
                            venueId = ticket.venueName ?: "",
                            categoryId = "",
                            time = ticket.eventTime ?: "",
                            minPrice = ticket.price
                        )
                    rootNavigator.push(EventDetailScreen(event.id))
                }
            )
            Spacer(Modifier.height(96.dp))
        }
    }
}

// ─── Pager билетов ─────────────────────────────────────────────────────────────

@Composable
private fun TicketsPager(
    tickets: List<TicketDto>,
    isArchived: Boolean,
    modifier: Modifier = Modifier,
    onTicketClick: (TicketDto) -> Unit
) {
    val pagerState = rememberPagerState { tickets.size }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            TicketCard(
                ticket = tickets[page],
                isArchived = isArchived,
                onClick = { onTicketClick(tickets[page]) }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (tickets.size > 1) {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(tickets.size) { i ->
                    val active = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (active) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

// ─── Карточка билета ───────────────────────────────────────────────────────────

@Composable
private fun TicketCard(ticket: TicketDto, isArchived: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val palettes = listOf(
                listOf(Color(0xFF1A1A2E), Color(0xFF16213E)),
                listOf(Color(0xFF0F3460), Color(0xFF533483)),
                listOf(Color(0xFF2D6A4F), Color(0xFF1B4332)),
                listOf(Color(0xFF6A0572), Color(0xFF3A0CA3))
            )
            val palette = palettes[kotlin.math.abs(ticket.id.hashCode()) % palettes.size]
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(palette))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    ticket.eventLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                if (ticket.venueName != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.LocationOn, null,
                            tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                        Text(ticket.venueName, style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                    Spacer(Modifier.height(2.dp))
                }
                if (ticket.eventTime != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Outlined.DateRange, null,
                            tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                        Text(ticket.eventTime, style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        if (isArchived) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Мероприятие завершилось",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (ticket.eventTime != null) {
                        Text(
                            ticket.eventTime,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (ticket.seat != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Outlined.ConfirmationNumber, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Text(ticket.seat, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(12.dp))
                }

                var showQrFullscreen by remember { mutableStateOf(false) }

                QrCode(
                    modifier = Modifier
                        .size(160.dp)
                        .clickable { showQrFullscreen = true }
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Нажмите на QR, чтобы увеличить",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                if (showQrFullscreen) {
                    QrFullscreenDialog(onDismiss = { showQrFullscreen = false })
                }
            }
        }
    }
}

// ─── QR-код ────────────────────────────────────────────────────────────────────

// QR-паттерн вынесен на уровень файла — один экземпляр для всех вызовов
private val QR_PATTERN = listOf(
    listOf(1,1,1,1,1,1,1,0,1,0,1,0,1,1,1,1,1,1,1),
    listOf(1,0,0,0,0,0,1,0,0,1,0,1,1,0,0,0,0,0,1),
    listOf(1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1),
    listOf(1,0,1,1,1,0,1,0,0,1,1,0,1,0,1,1,1,0,1),
    listOf(1,0,1,1,1,0,1,0,1,1,0,1,1,0,1,1,1,0,1),
    listOf(1,0,0,0,0,0,1,0,0,0,1,0,1,0,0,0,0,0,1),
    listOf(1,1,1,1,1,1,1,0,1,0,1,0,1,1,1,1,1,1,1),
    listOf(0,0,0,0,0,0,0,0,1,1,0,1,0,0,0,0,0,0,0),
    listOf(1,0,1,1,0,1,1,1,0,1,1,0,1,1,0,1,1,1,0),
    listOf(0,1,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,0,1),
    listOf(1,1,0,1,0,1,1,0,1,1,0,0,1,0,1,1,0,1,1),
    listOf(0,0,0,0,0,0,0,0,1,0,1,1,0,1,1,0,1,0,0),
    listOf(1,1,1,1,1,1,1,0,0,1,0,1,1,0,0,1,0,1,0),
    listOf(1,0,0,0,0,0,1,0,1,0,1,0,0,1,1,0,1,0,1),
    listOf(1,0,1,1,1,0,1,0,0,1,1,1,0,1,0,1,0,1,0),
    listOf(1,0,1,1,1,0,1,0,1,0,0,0,1,0,1,0,1,0,1),
    listOf(1,0,1,1,1,0,1,0,0,1,0,1,1,1,0,1,1,1,0),
    listOf(1,0,0,0,0,0,1,0,1,1,1,0,0,0,1,0,0,0,1),
    listOf(1,1,1,1,1,1,1,0,0,0,1,1,0,1,0,1,0,1,0),
)

/**
 * Отрисовка QR-кода.
 * Ячейка: [cellDp]dp, зазор [gapDp]dp.
 * Итоговый размер сетки: 19 * cellDp + 18 * gapDp.
 * При cellDp=6, gapDp=1 → 114 + 18 = 132dp — помещается в 136dp (160 - 2*12 padding).
 */
@Composable
private fun QrCode(modifier: Modifier = Modifier, cellDp: Float = 6f, gapDp: Float = 1f) {
    // Белый фон с rounded corners — clip ДО background, чтобы скруглить именно фон
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(gapDp.dp)) {
            QR_PATTERN.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gapDp.dp)) {
                    row.forEach { cell ->
                        Box(
                            Modifier
                                .size(cellDp.dp)
                                .background(
                                    color = if (cell == 1) Color.Black else Color.White,
                                    shape = RoundedCornerShape(1.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

// ─── Fullscreen QR диалог ──────────────────────────────────────────────────────

@Composable
private fun QrFullscreenDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // QR увеличен: cellDp=14, gapDp=2 → 19*14 + 18*2 = 266+36 = 302dp + 32dp padding = 334dp
                QrCode(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { /* не пропускать клик наружу */ },
                    cellDp = 14f,
                    gapDp = 2f
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Нажмите для закрытия",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Кнопка закрыть
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Закрыть",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ─── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyTickets(isUpcoming: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.ConfirmationNumber,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isUpcoming) "Нет актуальных билетов" else "Нет архивных билетов",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isUpcoming) "Купите билет на любое событие\nв разделе «Афиша»"
            else "Здесь появятся события, которые\nвы уже посетили",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Оффлайн-баннер ────────────────────────────────────────────────────────────

@Composable
fun OfflineBanner(message: String = "Нет подключения · показаны сохранённые данные") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "⚠",
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

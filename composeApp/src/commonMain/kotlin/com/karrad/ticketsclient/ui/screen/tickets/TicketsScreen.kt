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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.MockTicket
import com.karrad.ticketsclient.TicketStatus

@Composable
fun TicketsScreen() {
    val allTickets = AppSession.mockTickets
    var selectedTab by remember { mutableIntStateOf(0) }

    val upcoming = allTickets.filter { it.status == TicketStatus.UPCOMING }
    val archived = allTickets.filter { it.status == TicketStatus.USED }
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

        // ─── Контент ─────────────────────────────────────────────────────────
        if (current.isEmpty()) {
            EmptyTickets(selectedTab == 0)
        } else {
            TicketsPager(tickets = current, isArchived = selectedTab == 1)
        }
    }
}

// ─── Pager билетов ─────────────────────────────────────────────────────────────

@Composable
private fun TicketsPager(tickets: List<MockTicket>, isArchived: Boolean) {
    val pagerState = rememberPagerState { tickets.size }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            TicketCard(ticket = tickets[page], isArchived = isArchived)
        }

        Spacer(Modifier.height(16.dp))

        // Dot indicators
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
private fun TicketCard(ticket: MockTicket, isArchived: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Фото-шапка с градиентом
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            // Градиентный placeholder
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
            // Информация поверх изображения
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    ticket.eventName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.LocationOn, null,
                        tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                    Text(ticket.venue, style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f))
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.DateRange, null,
                        tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                    Text(ticket.datetime, style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // QR-блок / архивный текст
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
                    Text(
                        ticket.datetime,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Место
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

                // QR-код (большой)
                QrCode(modifier = Modifier.size(160.dp))

                Spacer(Modifier.height(8.dp))

                Text(
                    "Покажите QR-код на входе",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ─── QR-код ────────────────────────────────────────────────────────────────────

@Composable
private fun QrCode(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Детальный паттерн QR
        val pattern = listOf(
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
        Column(verticalArrangement = Arrangement.spacedBy(1.5.dp)) {
            pattern.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
                    row.forEach { cell ->
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(if (cell == 1) Color.Black else Color.White)
                        )
                    }
                }
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


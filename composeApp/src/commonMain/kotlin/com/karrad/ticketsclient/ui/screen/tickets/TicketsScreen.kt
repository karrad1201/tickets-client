package com.karrad.ticketsclient.ui.screen.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
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
    val tabs = listOf("Предстоящие", "Прошедшие")

    val filtered = if (selectedTab == 0)
        allTickets.filter { it.status == TicketStatus.UPCOMING }
    else
        allTickets.filter { it.status == TicketStatus.USED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        Text(
            text = "Мои билеты",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // ─── Tabs ─────────────────────────────────────────────────────────────
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // ─── Content ──────────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            EmptyTickets(selectedTab == 0)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp, vertical = 16.dp
                )
            ) {
                items(filtered, key = { it.id }) { ticket ->
                    TicketCard(ticket = ticket)
                }
            }
        }
    }
}

// ─── Ticket card ───────────────────────────────────────────────────────────────

@Composable
private fun TicketCard(ticket: MockTicket) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Top colored strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            if (ticket.status == TicketStatus.UPCOMING)
                                listOf(Color(0xFF6C63FF), Color(0xFF00B4D8))
                            else
                                listOf(Color(0xFF888888), Color(0xFFAAAAAA))
                        )
                    )
            )

            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = ticket.eventName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(ticket.status)
                }

                Spacer(Modifier.height(10.dp))

                TicketInfoRow(
                    icon = Icons.Outlined.LocationOn,
                    text = ticket.venue
                )
                Spacer(Modifier.height(4.dp))
                TicketInfoRow(
                    icon = Icons.Outlined.DateRange,
                    text = ticket.datetime
                )
                Spacer(Modifier.height(4.dp))
                TicketInfoRow(
                    icon = Icons.Outlined.ConfirmationNumber,
                    text = ticket.seat
                )

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // QR placeholder + price
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QrPlaceholder()
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${ticket.price.formatPrice()} ₽",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "за 1 место",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TicketStatus) {
    val (text, bg, fg) = when (status) {
        TicketStatus.UPCOMING -> Triple(
            "Скоро",
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.primary
        )
        TicketStatus.USED -> Triple(
            "Использован",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = fg)
    }
}

@Composable
private fun TicketInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon, null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QrPlaceholder() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // Имитация QR-кода сеткой точек
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val pattern = listOf(
                listOf(1,1,1,0,1,1,1),
                listOf(1,0,1,0,1,0,1),
                listOf(1,1,1,1,1,1,1),
                listOf(0,1,0,1,0,1,0),
                listOf(1,1,1,0,1,1,1),
                listOf(1,0,0,1,0,0,1),
                listOf(1,1,1,0,1,1,1),
            )
            pattern.forEach { rowData ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    rowData.forEach { cell ->
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    if (cell == 1) MaterialTheme.colorScheme.onSurface
                                    else Color.Transparent
                                )
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
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isUpcoming) "Нет предстоящих событий" else "Нет прошедших событий",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isUpcoming) "Купите билет на любое событие\nиз раздела «Афиша»"
                   else "Здесь появятся события,\nкоторые вы уже посетили",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun Int.formatPrice(): String {
    val s = this.toString()
    return if (s.length <= 3) s else s.dropLast(3) + "\u00A0" + s.takeLast(3)
}

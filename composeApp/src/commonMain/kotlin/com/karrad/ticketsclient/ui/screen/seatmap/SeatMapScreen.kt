package com.karrad.ticketsclient.ui.screen.seatmap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.ui.util.formatPrice

// ─── Seat model ────────────────────────────────────────────────────────────────

private data class Seat(
    val row: Int,
    val col: Int,
    val zone: SeatZone
)

private enum class SeatZone(val label: String, val color: Color, val price: Int) {
    VIP("VIP", Color(0xFFFFD700), 5000),
    PARQUET("Партер", Color(0xFF6C63FF), 2500),
    BALCONY("Балкон", Color(0xFF00B4D8), 1200),
    UNAVAILABLE("", Color(0xFF888888), 0)
}

// ─── Layout generation ─────────────────────────────────────────────────────────

private fun buildSeats(): List<Seat> {
    val seats = mutableListOf<Seat>()
    // VIP — ряды 1-2, cols 3-10
    for (row in 1..2) for (col in 3..10) seats += Seat(row, col, SeatZone.VIP)
    // Parquet — ряды 3-8, cols 1-12
    for (row in 3..8) for (col in 1..12) {
        val zone = if ((row + col) % 7 == 0) SeatZone.UNAVAILABLE else SeatZone.PARQUET
        seats += Seat(row, col, zone)
    }
    // Balcony — ряды 10-13, cols 2-11
    for (row in 10..13) for (col in 2..11) {
        val zone = if ((row * col) % 5 == 0) SeatZone.UNAVAILABLE else SeatZone.BALCONY
        seats += Seat(row, col, zone)
    }
    return seats
}

private val SESSION_TIMES = listOf("13:00", "17:00", "20:00", "23:00")

// ─── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun SeatMapScreen(eventId: String) {
    val navigator = LocalNavigator.currentOrThrow
    val event = AppSession.currentEvent

    val allSeats = remember { buildSeats() }
    var selectedTime by remember { mutableStateOf(SESSION_TIMES[1]) }
    var selectedSeats by remember { mutableStateOf(setOf<Seat>()) }

    // pinch-zoom + pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.7f, 3f)
        offset += panChange
    }

    val totalPrice = selectedSeats.sumOf { it.zone.price }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ─── Toolbar ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
                Text(
                    text = event?.label ?: "Выбор мест",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    maxLines = 1
                )
            }

            // ─── Time chips ──────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SESSION_TIMES) { time ->
                    FilterChip(
                        selected = time == selectedTime,
                        onClick = {
                            selectedTime = time
                            selectedSeats = emptySet()
                        },
                        label = { Text(time) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // ─── Seat map (pinch-zoom) ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .transformable(state = transformState)
                    .clip(RoundedCornerShape(0.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SeatGrid(
                        seats = allSeats,
                        selected = selectedSeats,
                        onSeatClick = { seat ->
                            if (seat.zone == SeatZone.UNAVAILABLE) return@SeatGrid
                            selectedSeats = if (seat in selectedSeats)
                                selectedSeats - seat
                            else
                                selectedSeats + seat
                        }
                    )
                }
            }

            // ─── Legend ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf(
                    SeatZone.VIP to "VIP ${SeatZone.VIP.price.formatPrice()} ₽",
                    SeatZone.PARQUET to "Партер ${SeatZone.PARQUET.price.formatPrice()} ₽",
                    SeatZone.BALCONY to "Балкон ${SeatZone.BALCONY.price.formatPrice()} ₽"
                ).forEach { (zone, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(zone.color)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // padding for sticky button
            Spacer(Modifier.height(80.dp))
        }

        // ─── Sticky bottom CTA ────────────────────────────────────────────────
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
                if (selectedSeats.isNotEmpty()) {
                    Text(
                        text = "${selectedSeats.size} мест · ${totalPrice.formatPrice()} ₽",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                ElevatedButton(
                    onClick = { /* TODO: оформление заказа */ },
                    enabled = selectedSeats.isNotEmpty(),
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (selectedSeats.isEmpty()) "Выберите места"
                        else "Купить билеты · ${totalPrice.formatPrice()} ₽",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// ─── Seat grid ─────────────────────────────────────────────────────────────────

@Composable
private fun SeatGrid(
    seats: List<Seat>,
    selected: Set<Seat>,
    onSeatClick: (Seat) -> Unit
) {
    val seatSize = 22.dp
    val gap = 4.dp
    val stepX = seatSize + gap
    val stepY = seatSize + gap

    val maxRow = seats.maxOf { it.row }
    val maxCol = seats.maxOf { it.col }

    val totalWidth = stepX * (maxCol + 1)
    val totalHeight = stepY * (maxRow + 2)

    Box(
        modifier = Modifier
            .width(totalWidth)
            .height(totalHeight)
    ) {
        // Stage label
        Text(
            text = "С Ц Е Н А",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 0.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Seats
        seats.forEach { seat ->
            val isSelected = seat in selected
            val color = when {
                seat.zone == SeatZone.UNAVAILABLE -> SeatZone.UNAVAILABLE.color.copy(alpha = 0.4f)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> seat.zone.color
            }
            Box(
                modifier = Modifier
                    .offset(
                        x = stepX * seat.col,
                        y = stepY * seat.row + seatSize
                    )
                    .size(seatSize)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .then(
                        if (isSelected)
                            Modifier.border(2.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(4.dp))
                        else Modifier
                    )
                    .clickable(enabled = seat.zone != SeatZone.UNAVAILABLE) { onSeatClick(seat) }
            )
        }
    }
}


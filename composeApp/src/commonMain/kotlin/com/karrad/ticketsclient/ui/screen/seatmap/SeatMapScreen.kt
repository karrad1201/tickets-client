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
import androidx.compose.material.icons.outlined.CalendarMonth
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

// ─── Модели ────────────────────────────────────────────────────────────────────

private data class Seat(val row: Int, val col: Int, val available: Boolean)

private val SESSION_TIMES = listOf("13:00", "17:00", "23:00")

private fun buildSeats(): List<Seat> {
    val seats = mutableListOf<Seat>()
    // 8 рядов x 10 мест, некоторые недоступны
    for (row in 0..7) {
        for (col in 0..9) {
            val available = !((row == 2 && col in 3..5) ||
                    (row == 5 && col in 6..8) ||
                    (row == 1 && col == 8) ||
                    (row == 4 && col == 2) ||
                    (row == 6 && col in 0..1) ||
                    (row == 7 && col in 7..9))
            seats += Seat(row, col, available)
        }
    }
    return seats
}

// ─── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun SeatMapScreen(eventId: String) {
    val navigator = LocalNavigator.currentOrThrow
    val event = AppSession.currentEvent

    val allSeats = remember { buildSeats() }
    var selectedTime by remember { mutableStateOf(SESSION_TIMES[1]) }
    var selectedSeats by remember { mutableStateOf(setOf<Seat>()) }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.6f, 3f)
        offset += panChange
    }

    val seatPrice = 1400
    val totalPrice = selectedSeats.size * seatPrice

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ─── Toolbar ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { navigator.pop() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
                Text(
                    text = "Купить билеты",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = "Дата",
                        tint = MaterialTheme.colorScheme.primary)
                }
            }

            // ─── Чипы времени ────────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(SESSION_TIMES) { time ->
                    TimeChip(
                        time = time,
                        selected = time == selectedTime,
                        onClick = { selectedTime = time; selectedSeats = emptySet() }
                    )
                }
            }

            // ─── Схема зала ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .transformable(state = transformState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale, scaleY = scale,
                            translationX = offset.x, translationY = offset.y
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    SeatGrid(
                        seats = allSeats,
                        selected = selectedSeats,
                        onSeatClick = { seat ->
                            if (!seat.available) return@SeatGrid
                            selectedSeats = if (seat in selectedSeats) selectedSeats - seat
                            else selectedSeats + seat
                        }
                    )
                }

                // ─── +/- кнопки (правый край) ────────────────────────────────
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ZoomButton("+") { scale = (scale * 1.2f).coerceAtMost(3f) }
                    ZoomButton("−") { scale = (scale / 1.2f).coerceAtLeast(0.6f) }
                }
            }

            Spacer(Modifier.height(80.dp))
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
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (selectedSeats.isEmpty())
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.primary
                        )
                        .clickable(enabled = selectedSeats.isNotEmpty()) { },
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
                            "Купить билеты",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        if (selectedSeats.isNotEmpty()) {
                            Text(
                                "${totalPrice.formatPrice()} ₽",
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Time chip ─────────────────────────────────────────────────────────────────

@Composable
private fun TimeChip(time: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}

// ─── +/- кнопка ───────────────────────────────────────────────────────────────

@Composable
private fun ZoomButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// ─── Схема зала ────────────────────────────────────────────────────────────────

@Composable
private fun SeatGrid(
    seats: List<Seat>,
    selected: Set<Seat>,
    onSeatClick: (Seat) -> Unit
) {
    val dotSize = 20.dp
    val gap = 8.dp
    val step = dotSize + gap

    val rows = seats.maxOf { it.row } + 1
    val cols = seats.maxOf { it.col } + 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                repeat(cols) { col ->
                    val seat = seats.find { it.row == row && it.col == col }
                    val isSelected = seat != null && seat in selected
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    seat == null       -> Color.Transparent
                                    isSelected         -> MaterialTheme.colorScheme.primary
                                    !seat.available    -> Color(0xFFCCCCCC)
                                    else               -> Color(0xFF1C1C1E)
                                }
                            )
                            .then(
                                if (seat != null && seat.available && !isSelected)
                                    Modifier.clickable { onSeatClick(seat) }
                                else if (isSelected)
                                    Modifier.clickable { onSeatClick(seat!!) }
                                else Modifier
                            )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Дуга «Сцена» ────────────────────────────────────────────────────
        Box(contentAlignment = Alignment.Center) {
            // Имитация дуги через скруглённую полосу
            Box(
                modifier = Modifier
                    .width((cols * (dotSize.value + gap.value) - gap.value).dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFCCCCCC))
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
            ) { }
        }
        Text(
            "Сцена",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

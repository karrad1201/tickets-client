package com.karrad.ticketsclient.ui.screen.tickettype

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.OrderConfirmScreen
import com.karrad.ticketsclient.ui.util.formatPrice
import kotlinx.coroutines.launch

// ─── Мок-данные ────────────────────────────────────────────────────────────────

private data class TicketType(
    val id: String,
    val name: String,
    val description: String,
    val price: Int
)

private val MOCK_TICKET_TYPES = listOf(
    TicketType("tt-1", "Входной билет", "Полный доступ, весь день с 10:00 до 22:00", 500),
    TicketType("tt-2", "Льготный билет", "Для студентов, пенсионеров и детей до 14 лет", 250),
    TicketType("tt-3", "Семейный билет", "2 взрослых + 2 ребёнка (до 14 лет)", 1200)
)

private val MOCK_DATES = listOf(
    "8 апр", "9 апр", "10 апр", "11 апр", "12 апр", "13 апр", "14 апр"
)

// ─── Экран ─────────────────────────────────────────────────────────────────────

@Composable
fun TicketTypeScreen(eventId: String) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    var selectedDate by remember { mutableStateOf(MOCK_DATES.first()) }
    // qty per ticket type id
    val quantities = remember { mutableMapOf<String, Int>().also { m -> MOCK_TICKET_TYPES.forEach { m[it.id] = 0 } } }
    var totalPrice by remember { mutableIntStateOf(0) }
    var buyLoading by remember { mutableStateOf(false) }

    fun recalcTotal() {
        totalPrice = MOCK_TICKET_TYPES.sumOf { (quantities[it.id] ?: 0) * it.price }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {

            // ─── TopBar ──────────────────────────────────────────────────────
            Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        "Купить билеты",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // ─── Дата ────────────────────────────────────────────────────────
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MOCK_DATES) { date ->
                    val selected = date == selectedDate
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedDate = date }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            date,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ─── Типы билетов ────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(MOCK_TICKET_TYPES) { ticket ->
                    var qty by remember(ticket.id) { mutableIntStateOf(quantities[ticket.id] ?: 0) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Инфо о типе билета
                        Column(Modifier.weight(1f)) {
                            Text(
                                ticket.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                ticket.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${ticket.price.formatPrice()} ₽",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        // Счётчик ─  [−] N [+]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Кнопка −
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (qty > 0) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant,
                                        CircleShape
                                    )
                                    .clickable(enabled = qty > 0) {
                                        qty--
                                        quantities[ticket.id] = qty
                                        recalcTotal()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "−",
                                    fontSize = 18.sp,
                                    color = if (qty > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Количество
                            Text(
                                qty.toString(),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.width(24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            // Кнопка +
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable {
                                        qty++
                                        quantities[ticket.id] = qty
                                        recalcTotal()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("＋", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // ─── Sticky CTA ──────────────────────────────────────────────────────
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
                    .background(
                        if (totalPrice > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
                    .clickable(enabled = totalPrice > 0 && !buyLoading) {
                        buyLoading = true
                        scope.launch {
                            try {
                                val order = AppContainer.orderService.createOrder(
                                    eventId = eventId,
                                    authToken = AppSession.authToken ?: ""
                                )
                                navigator.push(
                                    OrderConfirmScreen(
                                        eventId = eventId,
                                        orderId = order.id,
                                        totalPrice = totalPrice
                                    )
                                )
                            } catch (_: Exception) {
                            } finally {
                                buyLoading = false
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (buyLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
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
                        if (totalPrice > 0) {
                            Text(
                                "${totalPrice.formatPrice()} ₽",
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
}

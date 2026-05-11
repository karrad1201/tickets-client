package com.karrad.ticketsclient.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.MainScreen
import com.karrad.ticketsclient.ui.util.formatEventDateFull
import com.karrad.ticketsclient.ui.util.formatPrice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val POLL_INTERVAL_MS = 3_000L

@Composable
fun OrderConfirmScreen(eventId: String, orderId: String, totalPrice: Int) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    var event by remember { mutableStateOf<EventDto?>(null) }
    var paymentUrl by remember { mutableStateOf<String?>(null) }
    var orderStatus by remember { mutableStateOf("PENDING_PAYMENT") }
    var polling by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val isMock = paymentUrl?.contains("mock-payments.local") == true
    val isPaid = orderStatus == "PAID"
    val isTerminal = orderStatus in setOf("PAID", "EXPIRED", "PAYMENT_FAILED")

    // Загружаем мероприятие и начальный статус заказа
    LaunchedEffect(orderId) {
        event = try { AppContainer.eventService.getEvent(eventId) } catch (e: Exception) { CrashReporter.log(e); null }
        try {
            val order = AppContainer.orderService.getOrder(orderId)
            paymentUrl = order.paymentUrl
            orderStatus = order.status
        } catch (e: Exception) { CrashReporter.log(e) }
    }

    // Поллинг статуса пока ожидаем подтверждение T-Банка
    LaunchedEffect(polling) {
        if (!polling) return@LaunchedEffect
        while (!isTerminal) {
            delay(POLL_INTERVAL_MS)
            try {
                orderStatus = AppContainer.orderService.getOrder(orderId).status
            } catch (e: Exception) {
                CrashReporter.log(e)
            }
            if (orderStatus in setOf("PAID", "EXPIRED", "PAYMENT_FAILED")) break
        }
        polling = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // ─── Toolbar ─────────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navigator.pop() }, enabled = !polling) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                "Подтверждение заказа",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(Modifier.height(24.dp))

        when {
            // ─── Успех ───────────────────────────────────────────────────────
            isPaid -> Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Оплата прошла успешно!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Билет добавлен в раздел «Мои билеты»",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { navigator.replaceAll(MainScreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Перейти к билетам", modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            // ─── Ошибка / истёк ──────────────────────────────────────────────
            isTerminal -> Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))
                Icon(
                    Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    when (orderStatus) {
                        "EXPIRED" -> "Время оплаты истекло"
                        else -> "Оплата не прошла"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Места освобождены. Попробуйте оформить заказ заново.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))
                OutlinedButton(
                    onClick = { navigator.pop() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Вернуться")
                }
            }

            // ─── Ожидание оплаты (поллинг) ───────────────────────────────────
            polling -> Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(48.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Ожидаем подтверждение оплаты…",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center
                )
                Text(
                    "После оплаты в Т-Банк вернитесь в приложение",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { navigator.pop() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Отмена")
                }
            }

            // ─── Детали заказа + кнопка оплаты ──────────────────────────────
            else -> {
                Text(
                    "Детали заказа",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(16.dp))

                OrderRow(label = "Событие", value = event?.label ?: "…")
                event?.time?.formatEventDateFull()?.let { dateStr ->
                    OrderRow(label = "Дата", value = dateStr)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                OrderRow(label = "Номер заказа", value = orderId.takeLast(8).uppercase())
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                OrderRow(
                    label = "К оплате",
                    value = "${totalPrice.formatPrice()} ₽",
                    valueWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(32.dp))

                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (isMock) {
                    // Devstack: мок-оплата без реального шлюза
                    Button(
                        onClick = {
                            error = null
                            scope.launch {
                                try {
                                    AppContainer.orderService.confirmPayment(orderId)
                                    orderStatus = "PAID"
                                } catch (e: Exception) {
                                    CrashReporter.log(e)
                                    error = "Ошибка оплаты. Попробуйте ещё раз."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF888888))
                    ) {
                        Text("[DEV] Симулировать оплату ${totalPrice.formatPrice()} ₽", modifier = Modifier.padding(vertical = 4.dp))
                    }
                } else {
                    // Prod: открываем T-Банк в браузере, переходим в режим поллинга
                    Button(
                        onClick = {
                            paymentUrl?.let { url ->
                                uriHandler.openUri(url)
                                polling = true
                            }
                        },
                        enabled = paymentUrl != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    ) {
                        if (paymentUrl == null) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Оплатить ${totalPrice.formatPrice()} ₽", modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderRow(
    label: String,
    value: String,
    valueWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = valueWeight),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

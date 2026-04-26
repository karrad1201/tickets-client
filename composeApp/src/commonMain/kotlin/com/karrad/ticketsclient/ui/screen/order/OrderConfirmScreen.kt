package com.karrad.ticketsclient.ui.screen.order

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.MainScreen
import com.karrad.ticketsclient.ui.util.formatPrice
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun OrderConfirmScreen(eventId: String, orderId: String, totalPrice: Int) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    var event by remember { mutableStateOf<EventDto?>(null) }
    var orderStatus by remember { mutableStateOf("PENDING_PAYMENT") }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        event = try { AppContainer.eventService.getEvent(eventId) } catch (e: Exception) { CrashReporter.log(e); null }
    }

    LaunchedEffect(orderId) {
        try {
            orderStatus = AppContainer.orderService.getOrder(orderId).status
        } catch (e: Exception) { CrashReporter.log(e) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // ─── Toolbar ─────────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navigator.pop() }, enabled = !loading) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                "Подтверждение заказа",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(Modifier.height(24.dp))

        if (success) {
            // ─── Success state ────────────────────────────────────────────────
            Column(
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
        } else {
            // ─── Order details ────────────────────────────────────────────────
            Text(
                "Детали заказа",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(16.dp))

            OrderRow(label = "Событие", value = event?.label ?: "…")
            event?.time?.let { iso ->
                val dt = try {
                    Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault())
                } catch (_: Exception) { null }
                if (dt != null) {
                    val dateStr = "%02d.%02d.%04d %02d:%02d".format(
                        dt.dayOfMonth, dt.monthNumber, dt.year, dt.hour, dt.minute
                    )
                    OrderRow(label = "Дата", value = dateStr)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            OrderRow(label = "Номер заказа", value = orderId.takeLast(8).uppercase())
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            OrderRow(
                label = "Статус",
                value = when (orderStatus) {
                    "PENDING_PAYMENT" -> "Ожидает оплаты"
                    "PAID" -> "Оплачен"
                    "EXPIRED" -> "Истёк"
                    "PAYMENT_FAILED" -> "Ошибка оплаты"
                    else -> orderStatus
                }
            )
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
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    loading = true
                    error = null
                    scope.launch {
                        try {
                            AppContainer.orderService.confirmPayment(orderId)
                            success = true
                        } catch (e: Exception) {
                            CrashReporter.log(e)
                            error = "Ошибка оплаты. Попробуйте ещё раз."
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Оплатить ${totalPrice.formatPrice()} ₽", modifier = Modifier.padding(vertical = 4.dp))
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

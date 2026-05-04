package com.karrad.ticketsclient.ui.screen.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.TicketValidationResponse
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.launch

// ─── State ─────────────────────────────────────────────────────────────────────

private sealed interface ScannerState {
    data object Loading : ScannerState
    data object NoAccess : ScannerState
    data class EventList(val events: List<OrgEventItem>) : ScannerState
    data class Scanning(
        val event: OrgEventItem,
        val validating: Boolean = false,
        val result: TicketValidationResponse? = null
    ) : ScannerState
}

// ─── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ScannerScreen() {
    var state by remember { mutableStateOf<ScannerState>(ScannerState.Loading) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val events = AppContainer.scannerService.getMyOrgEvents()
            state = if (events.isEmpty()) ScannerState.NoAccess else ScannerState.EventList(events)
        } catch (e: Exception) {
            CrashReporter.log(e)
            state = ScannerState.NoAccess
        }
    }

    when (val s = state) {
        is ScannerState.Loading -> LoadingContent()
        is ScannerState.NoAccess -> NoAccessContent()
        is ScannerState.EventList -> EventListContent(
            events = s.events,
            onEventSelected = { event -> state = ScannerState.Scanning(event) }
        )
        is ScannerState.Scanning -> ScanningContent(
            scanning = s,
            onBack = {
                scope.launch {
                    try {
                        val events = AppContainer.scannerService.getMyOrgEvents()
                        state = if (events.isEmpty()) ScannerState.NoAccess else ScannerState.EventList(events)
                    } catch (e: Exception) {
                        state = ScannerState.NoAccess
                    }
                }
            },
            onScanned = { ticketId ->
                if (!s.validating && s.result == null) {
                    state = s.copy(validating = true)
                    scope.launch {
                        try {
                            val result = AppContainer.scannerService.validateTicket(
                                eventId = s.event.id,
                                ticketId = ticketId
                            )
                            state = s.copy(validating = false, result = result)
                        } catch (e: Exception) {
                            CrashReporter.log(e)
                            state = s.copy(
                                validating = false,
                                result = TicketValidationResponse(status = "NOT_FOUND")
                            )
                        }
                    }
                }
            },
            onDismissResult = { state = s.copy(validating = false, result = null) }
        )
    }
}

// ─── Loading ───────────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

// ─── No Access ─────────────────────────────────────────────────────────────────

@Composable
private fun NoAccessContent() {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Block,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Нет доступа",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Нет доступных мероприятий для сканирования",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ─── Event List ────────────────────────────────────────────────────────────────

@Composable
private fun EventListContent(
    events: List<OrgEventItem>,
    onEventSelected: (OrgEventItem) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                "Сканер",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            "Выберите мероприятие для проверки билетов",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(events) { event ->
                EventRow(event = event, onClick = { onEventSelected(event) })
                if (event != events.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(96.dp))
    }
}

@Composable
private fun EventRow(event: OrgEventItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                event.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                event.time.formatEventTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

private fun String.formatEventTime(): String = try {
    val parts = this.split("T")
    if (parts.size == 2) {
        val date = parts[0].split("-")
        val time = parts[1].removeSuffix("Z").take(5)
        "${date[2]}.${date[1]}.${date[0]}, $time"
    } else this
} catch (e: Exception) { this }

// ─── Scanning ──────────────────────────────────────────────────────────────────

@Composable
private fun ScanningContent(
    scanning: ScannerState.Scanning,
    onBack: () -> Unit,
    onScanned: (String) -> Unit,
    onDismissResult: () -> Unit
) {
    // Increment to force camera recreation after each scan result is dismissed
    var cameraKey by remember { mutableStateOf(0) }

    Box(Modifier.fillMaxSize()) {
        // Camera view — пересоздаётся при dismiss результата
        androidx.compose.runtime.key(cameraKey) {
            QrScannerView(
                onScanned = onScanned,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Viewfinder overlay
        ViewfinderOverlay(modifier = Modifier.fillMaxSize())

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.50f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Назад",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                scanning.event.label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        // Validating indicator
        if (scanning.validating) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.40f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // Result overlay
        scanning.result?.let { result ->
            ValidationResultOverlay(
                result = result,
                onDismiss = {
                    cameraKey++
                    onDismissResult()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .padding(bottom = 96.dp)
            )
        }
    }
}

@Composable
private fun ViewfinderOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Semi-transparent corners are drawn via the background with a transparent center
        // Simple implementation: dark overlay with a centered transparent "window"
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black.copy(alpha = 0.55f))
                .align(Alignment.TopCenter)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black.copy(alpha = 0.55f))
                .align(Alignment.BottomCenter)
        )
    }
}

// ─── Result Overlay ────────────────────────────────────────────────────────────

@Composable
private fun ValidationResultOverlay(
    result: TicketValidationResponse,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, icon, title) = when (result.status) {
        "VALID" -> Triple(Color(0xFF1B5E20), Icons.Outlined.CheckCircle, "Проход разрешён")
        "ALREADY_USED" -> Triple(Color(0xFFB71C1C), Icons.Outlined.Error, "Билет уже использован")
        "WRONG_EVENT" -> Triple(Color(0xFFF57F17), Icons.Outlined.Schedule, "Билет на другое мероприятие")
        "UNAUTHORIZED" -> Triple(Color(0xFF37474F), Icons.Outlined.Block, "Нет доступа")
        else -> Triple(Color(0xFFB71C1C), Icons.Outlined.Error, "Билет не найден")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(Modifier.height(8.dp))

        when (result.status) {
            "VALID" -> {
                result.holderName?.let { ResultLine("Владелец", it) }
                result.seat?.let { ResultLine("Место", it) }
            }
            "ALREADY_USED" -> {
                result.holderName?.let { ResultLine("Владелец", it) }
                result.usedAt?.let { ResultLine("Использован", it.formatUsedAt()) }
            }
            "WRONG_EVENT" -> {
                result.ticketEventLabel?.let { ResultLine("Билет на", it) }
                result.scannedEventLabel?.let { ResultLine("Текущее мероприятие", it) }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.25f),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Outlined.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Сканировать следующий")
        }
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.70f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )
    }
}

private fun String.formatUsedAt(): String = try {
    val parts = this.split("T")
    if (parts.size == 2) {
        val date = parts[0].split("-")
        val time = parts[1].removeSuffix("Z").take(5)
        "${date[2]}.${date[1]}.${date[0]} в $time"
    } else this
} catch (e: Exception) { this }

package com.karrad.ticketsclient.ui.screen.org

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.VenueAccessGrantDto
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.launch

@Composable
fun VenueAccessScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    var incoming by remember { mutableStateOf<List<VenueAccessGrantDto>>(emptyList()) }
    var outgoing by remember { mutableStateOf<List<VenueAccessGrantDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        scope.launch {
            isLoading = true
            error = null
            val token = AppSession.authToken ?: return@launch
            try {
                incoming = AppContainer.venueAccessGrantService.getIncomingRequests(token)
                outgoing = AppContainer.venueAccessGrantService.getOutgoingRequests(token)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigator.pop() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Назад")
            }
            Text(
                "Аренда площадок",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                text = { Text("Входящие (${incoming.size})") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                text = { Text("Исходящие (${outgoing.size})") })
        }

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                val items = if (selectedTab == 0) incoming else outgoing
                if (items.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (selectedTab == 0) "Входящих запросов нет"
                            else "Исходящих запросов нет",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        items(items) { grant ->
                            GrantCard(
                                grant = grant,
                                isIncoming = selectedTab == 0,
                                onApprove = {
                                    scope.launch {
                                        val token = AppSession.authToken ?: return@launch
                                        runCatching {
                                            AppContainer.venueAccessGrantService.approve(token, grant.venueId, grant.id)
                                        }
                                        load()
                                    }
                                },
                                onReject = {
                                    scope.launch {
                                        val token = AppSession.authToken ?: return@launch
                                        runCatching {
                                            AppContainer.venueAccessGrantService.reject(token, grant.venueId, grant.id)
                                        }
                                        load()
                                    }
                                }
                            )
                        }
                        item { Spacer(Modifier.height(96.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun GrantCard(
    grant: VenueAccessGrantDto,
    isIncoming: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = statusLabel(grant.status),
                style = MaterialTheme.typography.labelMedium,
                color = statusColor(grant.status)
            )
            Text(
                text = grant.createdAt.take(10),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (isIncoming) "Заявка от орг.: ${grant.requestingOrgId.take(8)}…"
                   else "Площадка: ${grant.venueId.take(8)}…",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
        if (isIncoming && grant.status == "PENDING") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApprove, modifier = androidx.compose.ui.Modifier.weight(1f)) {
                    Text("Одобрить")
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = androidx.compose.ui.Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Отклонить")
                }
            }
        }
    }
}

@Composable
private fun statusLabel(status: String): String = when (status) {
    "PENDING" -> "Ожидает"
    "APPROVED" -> "Одобрено"
    "REJECTED" -> "Отклонено"
    else -> status
}

@Composable
private fun statusColor(status: String) = when (status) {
    "PENDING" -> MaterialTheme.colorScheme.tertiary
    "APPROVED" -> MaterialTheme.colorScheme.primary
    "REJECTED" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

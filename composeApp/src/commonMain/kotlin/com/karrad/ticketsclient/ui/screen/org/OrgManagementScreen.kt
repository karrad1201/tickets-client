package com.karrad.ticketsclient.ui.screen.org

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.util.formatEventTime

@Composable
fun OrgManagementScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val vm = viewModel { OrgManagementViewModel(AppContainer.orgMemberService) }
    val state by vm.state.collectAsState()

    var showLeaveConfirm1 by remember { mutableStateOf(false) }
    var showLeaveConfirm2 by remember { mutableStateOf(false) }

    LaunchedEffect(state.leftOrg) {
        if (state.leftOrg) {
            AppSession.orgMembership = null
            navigator.pop()
        }
    }

    // Перезагружать список при возврате с дочерних экранов
    LaunchedEffect(navigator.items.size) {
        vm.loadMembers()
    }

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
                "Мероприятия",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { navigator.push(com.karrad.ticketsclient.ui.navigation.CreateEventScreen) }) {
                Icon(Icons.Outlined.Add, contentDescription = "Создать мероприятие")
            }
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                if (state.events.isNotEmpty()) {
                    items(state.events) { event ->
                        OrgEventRow(
                            event = event,
                            onClick = {
                                navigator.push(com.karrad.ticketsclient.ui.navigation.EventManagementScreen(event))
                            },
                            onSetupInventory = {
                                navigator.push(com.karrad.ticketsclient.ui.navigation.SetupInventoryScreen(event.id, event.venueSpaceId))
                            }
                        )
                    }
                    item { Spacer(Modifier.height(4.dp)) }
                }
                item {
                    OutlinedButton(
                        onClick = { navigator.push(com.karrad.ticketsclient.ui.navigation.VenueApplicationScreen) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.Domain, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Заявки на площадки")
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { showLeaveConfirm1 = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Outlined.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Выйти из организации")
                    }
                }
                if (state.leaveError != null) {
                    item {
                        Text(
                            text = if (state.leaveError!!.contains("SOLE_OWNER"))
                                "Вы единственный владелец. Назначьте другого владельца перед выходом."
                            else state.leaveError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                item { Spacer(Modifier.height(96.dp)) }
            }
        }
    }

    // Диалог подтверждения выхода — шаг 1
    if (showLeaveConfirm1) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirm1 = false },
            title = { Text("Выйти из организации?") },
            text = {
                Text("Вы потеряете доступ к управлению мероприятиями и площадками организации.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirm1 = false
                        showLeaveConfirm2 = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Продолжить") }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirm1 = false }) { Text("Отмена") }
            }
        )
    }

    // Диалог подтверждения выхода — шаг 2
    if (showLeaveConfirm2) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirm2 = false },
            title = { Text("Подтвердите выход") },
            text = {
                Text(
                    "Это действие нельзя отменить.\n\n" +
                    "Если вы являетесь владельцем, убедитесь, что передали роль OWNER другому участнику."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveConfirm2 = false
                        vm.leaveOrganization()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Выйти") }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirm2 = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun OrgEventRow(
    event: OrgEventItem,
    onClick: () -> Unit,
    onSetupInventory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                event.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )
            Text(
                buildString {
                    append(event.time.formatEventTime())
                    if (event.venueLabel != null) append(" · ${event.venueLabel}")
                    if (event.hasInventory) append(" · ${event.sold}/${event.capacity}")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(8.dp))
        if (!event.hasInventory) {
            OutlinedButton(
                onClick = onSetupInventory,
                modifier = Modifier.height(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
            ) {
                Text("Билеты", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun MemberRow(
    member: OrgMemberDto,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = member.role,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = member.fullName ?: member.phone ?: member.userId,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            if (member.phone != null && member.fullName != null) {
                Text(
                    text = member.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        if (canDelete) {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

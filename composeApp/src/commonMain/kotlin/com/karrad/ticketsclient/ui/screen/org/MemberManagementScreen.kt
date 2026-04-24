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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.karrad.ticketsclient.data.api.dto.OrgMemberDto
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.launch

@Composable
fun MemberManagementScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    var members by remember { mutableStateOf<List<OrgMemberDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var addUserId by remember { mutableStateOf("") }
    var addVenueId by remember { mutableStateOf("") }

    fun loadMembers() {
        scope.launch {
            isLoading = true
            error = null
            val token = AppSession.authToken ?: return@launch
            try {
                // MANAGER видит только STAFF
                members = AppContainer.orgMemberService.listMembers(token)
                    .filter { it.role == "STAFF" }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadMembers() }

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
                "Сотрудники",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Добавить сотрудника")
            }
        }

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
            }
            members.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Сотрудников пока нет",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(members) { member ->
                    MemberRow(
                        member = member,
                        canDelete = true,
                        onDelete = {
                            scope.launch {
                                val token = AppSession.authToken ?: return@launch
                                runCatching { AppContainer.orgMemberService.deleteMember(token, member.id) }
                                loadMembers()
                            }
                        }
                    )
                }
                item { Spacer(Modifier.height(96.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить сотрудника (STAFF)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = addUserId,
                        onValueChange = { addUserId = it },
                        label = { Text("UUID пользователя") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = addVenueId,
                        onValueChange = { addVenueId = it },
                        label = { Text("UUID площадки") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        val token = AppSession.authToken ?: return@launch
                        runCatching {
                            AppContainer.orgMemberService.addMember(
                                authToken = token,
                                userId = addUserId.trim(),
                                role = "STAFF",
                                venueId = addVenueId.trim().ifBlank { null }
                            )
                        }
                        showAddDialog = false
                        addUserId = ""
                        addVenueId = ""
                        loadMembers()
                    }
                }) { Text("Добавить") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Отмена") }
            }
        )
    }
}

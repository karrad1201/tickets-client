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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.screen.auth.normalizePhone

@Composable
fun MemberManagementScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val vm = viewModel { MemberManagementViewModel(AppContainer.orgMemberService) }
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var addPhone by remember { mutableStateOf("") }
    var addVenueId by remember { mutableStateOf("") }
    var venueMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.accountCreated) {
        if (state.accountCreated) {
            snackbarHostState.showSnackbar(
                "Создан новый аккаунт для $addPhone",
                duration = SnackbarDuration.Short
            )
            vm.clearAddFeedback()
        }
    }

    Box(Modifier.fillMaxSize()) {
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
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
                state.members.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Сотрудников пока нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(state.members) { member ->
                        MemberRow(
                            member = member,
                            canDelete = true,
                            onDelete = { vm.deleteMember(member.id) }
                        )
                    }
                    item { Spacer(Modifier.height(96.dp)) }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; vm.clearAddFeedback() },
            title = { Text("Добавить сотрудника (STAFF)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = addPhone,
                        onValueChange = { addPhone = it },
                        label = { Text("Номер телефона") },
                        placeholder = { Text("+79XXXXXXXXX") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.venues.isNotEmpty()) {
                        val selectedVenue = state.venues.firstOrNull { it.id == addVenueId }
                        Box {
                            OutlinedTextField(
                                value = selectedVenue?.label ?: "Выберите площадку",
                                onValueChange = {},
                                label = { Text("Площадка *") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            TextButton(
                                onClick = { venueMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(selectedVenue?.label ?: "Выберите площадку") }
                            DropdownMenu(
                                expanded = venueMenuExpanded,
                                onDismissRequest = { venueMenuExpanded = false }
                            ) {
                                state.venues.forEach { venue ->
                                    DropdownMenuItem(
                                        text = { Text(venue.label) },
                                        onClick = { addVenueId = venue.id; venueMenuExpanded = false }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = addVenueId,
                            onValueChange = { addVenueId = it },
                            label = { Text("ID площадки *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (state.addError != null) {
                        Text(
                            text = when {
                                state.addError!!.contains("ALREADY_MEMBER") -> "Пользователь уже состоит в организации"
                                else -> state.addError!!
                            },
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val phone = normalizePhone(addPhone.trim())
                        vm.addMemberByPhone(phone = phone, venueId = addVenueId.ifBlank { null })
                        showAddDialog = false
                        addPhone = ""; addVenueId = ""
                    },
                    enabled = addPhone.isNotBlank() && addVenueId.isNotBlank()
                ) { Text("Добавить") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; vm.clearAddFeedback() }) { Text("Отмена") }
            }
        )
    }
}

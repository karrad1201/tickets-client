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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.ui.navigation.LayoutTemplateBuilderScreen
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.CreateVenueSpaceRequest
import com.karrad.ticketsclient.data.api.dto.VenueSpaceDto
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.launch

@Composable
fun VenueSpacesScreen(venueId: String, venueLabel: String) {
    val scope = rememberCoroutineScope()
    val spaces = remember { mutableStateListOf<VenueSpaceDto>() }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(venueId) {
        loading = true
        runCatching { AppContainer.venueSpaceService.list(venueId) }
            .onSuccess { spaces.addAll(it) }
            .onFailure { error = it.message }
        loading = false
    }

    if (showAddDialog) {
        AddVenueSpaceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { label, type, capacity ->
                showAddDialog = false
                scope.launch {
                    runCatching {
                        AppContainer.venueSpaceService.add(
                            venueId,
                            CreateVenueSpaceRequest(label = label, type = type, capacity = capacity)
                        )
                    }.onSuccess { spaces.add(it) }
                        .onFailure { CrashReporter.log(it) }
                }
            }
        )
    }

    val navigator = LocalNavigator.currentOrThrow

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Залы и пространства",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    venueLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Добавить зал")
            }
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
            }
            spaces.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Нет залов / пространств", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Нажмите «+» чтобы добавить",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }
                items(spaces) { space ->
                    VenueSpaceRow(space, navigator)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun VenueSpaceRow(space: VenueSpaceDto, navigator: cafe.adriel.voyager.navigator.Navigator) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(space.label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(2.dp))
            val typeLabel = if (space.type == "SEATED") "С местами" else "Партер"
            val capacityLabel = if (space.capacity > 0) " · ${space.capacity} мест" else ""
            Text(
                "$typeLabel$capacityLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (space.type == "SEATED") {
            OutlinedButton(
                onClick = { navigator.push(LayoutTemplateBuilderScreen(space.id, space.label)) },
                modifier = Modifier.height(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
            ) {
                Text("Схема", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun AddVenueSpaceDialog(
    onDismiss: () -> Unit,
    onConfirm: (label: String, type: String, capacity: Int) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ADMISSION") }
    var capacityStr by remember { mutableStateOf("") }
    var typeMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить зал / пространство") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Box {
                    OutlinedTextField(
                        value = if (type == "SEATED") "С местами (SEATED)" else "Партер (ADMISSION)",
                        onValueChange = {},
                        label = { Text("Тип") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            TextButton(onClick = { typeMenuExpanded = true }) {
                                Text("Изм.")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Партер (ADMISSION)") },
                            onClick = { type = "ADMISSION"; typeMenuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("С местами (SEATED)") },
                            onClick = { type = "SEATED"; typeMenuExpanded = false }
                        )
                    }
                }

                OutlinedTextField(
                    value = capacityStr,
                    onValueChange = { if (it.all(Char::isDigit)) capacityStr = it },
                    label = { Text("Вместимость (0 — не ограничена)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(label.trim(), type, capacityStr.toIntOrNull() ?: 0) },
                enabled = label.isNotBlank()
            ) { Text("Добавить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

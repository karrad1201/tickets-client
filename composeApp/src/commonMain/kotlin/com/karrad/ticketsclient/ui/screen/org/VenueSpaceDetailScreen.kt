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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.CreateSpacePriceProfileRequest
import com.karrad.ticketsclient.data.api.dto.SectionPriceDto
import com.karrad.ticketsclient.data.api.dto.SpacePriceProfileDto
import com.karrad.ticketsclient.data.api.dto.TicketTypeTemplateDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.LayoutTemplateBuilderScreen
import kotlinx.coroutines.launch

@Composable
fun VenueSpaceDetailScreen(spaceId: String, spaceLabel: String, spaceType: String) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val profiles = remember { mutableStateListOf<SpacePriceProfileDto>() }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<SpacePriceProfileDto?>(null) }

    LaunchedEffect(spaceId) {
        loading = true
        runCatching { AppContainer.venueSpaceService.listPriceProfiles(spaceId) }
            .onSuccess { profiles.addAll(it) }
            .onFailure { error = it.message }
        loading = false
    }

    if (showAddDialog) {
        AddPriceProfileDialog(
            spaceType = spaceType,
            onDismiss = { showAddDialog = false },
            onConfirm = { request ->
                showAddDialog = false
                scope.launch {
                    runCatching { AppContainer.venueSpaceService.createPriceProfile(spaceId, request) }
                        .onSuccess { profiles.add(it) }
                        .onFailure { CrashReporter.log(it) }
                }
            }
        )
    }

    deleteTarget?.let { profile ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Удалить профиль?") },
            text = { Text("Профиль «${profile.label}» будет удалён.") },
            confirmButton = {
                Button(
                    onClick = {
                        val target = profile
                        deleteTarget = null
                        scope.launch {
                            runCatching { AppContainer.venueSpaceService.deletePriceProfile(spaceId, target.id) }
                                .onSuccess { profiles.remove(target) }
                                .onFailure { CrashReporter.log(it) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Удалить") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Отмена") } }
        )
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    spaceLabel,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    if (spaceType == "SEATED") "С местами" else "Партер",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (spaceType == "SEATED") {
                OutlinedButton(
                    onClick = { navigator.push(LayoutTemplateBuilderScreen(spaceId, spaceLabel)) },
                    modifier = Modifier.padding(end = 8.dp)
                ) { Text("Схема мест") }
            }
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
            }
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Ценовые профили",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Outlined.Add, contentDescription = "Добавить профиль")
                        }
                    }
                }

                if (profiles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Нет ценовых профилей.\nНажмите «+» чтобы добавить.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                items(profiles) { profile ->
                    PriceProfileRow(profile, onDelete = { deleteTarget = profile })
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun PriceProfileRow(profile: SpacePriceProfileDto, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(profile.label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(2.dp))
            val modeLabel = if (profile.mode == "SEATED") "С местами" else "Без рассадки"
            val detailLabel = when (profile.mode) {
                "SEATED" -> "${profile.sectionPrices.size} зон"
                else -> "${profile.ticketTypes.size} типов билетов"
            }
            Text(
                "$modeLabel · $detailLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private data class SectionPriceEntry(var key: String = "", var price: String = "")
private data class TicketTypeEntry(var label: String = "", var price: String = "", var quota: String = "")

@Composable
private fun AddPriceProfileDialog(
    spaceType: String,
    onDismiss: () -> Unit,
    onConfirm: (CreateSpacePriceProfileRequest) -> Unit
) {
    var profileLabel by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(spaceType) }
    var modeMenuExpanded by remember { mutableStateOf(false) }
    val sectionEntries = remember { mutableStateListOf(SectionPriceEntry()) }
    val ticketEntries = remember { mutableStateListOf(TicketTypeEntry()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить ценовой профиль") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = profileLabel,
                    onValueChange = { profileLabel = it },
                    label = { Text("Название профиля") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Mode selector
                Box {
                    OutlinedTextField(
                        value = if (mode == "SEATED") "С местами" else "Без рассадки",
                        onValueChange = {},
                        label = { Text("Режим") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            TextButton(onClick = { modeMenuExpanded = true }) { Text("Изм.") }
                        }
                    )
                    DropdownMenu(
                        expanded = modeMenuExpanded,
                        onDismissRequest = { modeMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("С местами (SEATED)") },
                            onClick = { mode = "SEATED"; modeMenuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Без рассадки (GA)") },
                            onClick = { mode = "GENERAL_ADMISSION"; modeMenuExpanded = false }
                        )
                    }
                }

                HorizontalDivider()

                if (mode == "SEATED") {
                    Text("Цены по секциям", style = MaterialTheme.typography.labelMedium)
                    sectionEntries.forEachIndexed { index, entry ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = entry.key,
                                onValueChange = { sectionEntries[index] = entry.copy(key = it) },
                                label = { Text("Секция") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = entry.price,
                                onValueChange = { if (it.all(Char::isDigit)) sectionEntries[index] = entry.copy(price = it) },
                                label = { Text("₽") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(8.dp)
                            )
                            if (sectionEntries.size > 1) {
                                IconButton(onClick = { sectionEntries.removeAt(index) }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    TextButton(onClick = { sectionEntries.add(SectionPriceEntry()) }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(" Добавить секцию")
                    }
                } else {
                    Text("Типы билетов", style = MaterialTheme.typography.labelMedium)
                    ticketEntries.forEachIndexed { index, entry ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = entry.label,
                                onValueChange = { ticketEntries[index] = entry.copy(label = it) },
                                label = { Text("Название") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = entry.price,
                                    onValueChange = { if (it.all(Char::isDigit)) ticketEntries[index] = entry.copy(price = it) },
                                    label = { Text("Цена ₽") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = entry.quota,
                                    onValueChange = { if (it.all(Char::isDigit)) ticketEntries[index] = entry.copy(quota = it) },
                                    label = { Text("Кол-во") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                if (ticketEntries.size > 1) {
                                    IconButton(onClick = { ticketEntries.removeAt(index) }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                        if (index < ticketEntries.size - 1) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                    TextButton(onClick = { ticketEntries.add(TicketTypeEntry()) }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(" Добавить тип")
                    }
                }
            }
        },
        confirmButton = {
            val canConfirm = profileLabel.isNotBlank() && if (mode == "SEATED")
                sectionEntries.all { it.key.isNotBlank() && it.price.isNotBlank() }
            else
                ticketEntries.all { it.label.isNotBlank() && it.price.isNotBlank() && it.quota.isNotBlank() }

            Button(
                onClick = {
                    val request = if (mode == "SEATED") {
                        CreateSpacePriceProfileRequest(
                            label = profileLabel.trim(),
                            mode = "SEATED",
                            sectionPrices = sectionEntries.map { SectionPriceDto(it.key.trim(), it.price.toInt()) }
                        )
                    } else {
                        CreateSpacePriceProfileRequest(
                            label = profileLabel.trim(),
                            mode = "GENERAL_ADMISSION",
                            ticketTypes = ticketEntries.map {
                                TicketTypeTemplateDto(it.label.trim(), it.price.toInt(), it.quota.toInt())
                            }
                        )
                    }
                    onConfirm(request)
                },
                enabled = canConfirm
            ) { Text("Создать") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

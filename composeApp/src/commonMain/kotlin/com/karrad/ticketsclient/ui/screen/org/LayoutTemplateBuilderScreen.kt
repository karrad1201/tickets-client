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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.karrad.ticketsclient.data.api.dto.CreateLayoutTemplateRequest
import com.karrad.ticketsclient.data.api.dto.RowDto
import com.karrad.ticketsclient.data.api.dto.SectionDto
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.launch

@Composable
fun LayoutTemplateBuilderScreen(venueSpaceId: String, venueSpaceLabel: String) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    var templateLabel by remember { mutableStateOf("") }
    val sections = remember { mutableStateListOf<SectionDraft>() }

    var showAddSectionDialog by remember { mutableStateOf(false) }
    var addRowForSectionIndex by remember { mutableStateOf<Int?>(null) }

    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    if (showAddSectionDialog) {
        AddSectionDialog(
            onDismiss = { showAddSectionDialog = false },
            onConfirm = { label, key ->
                sections.add(SectionDraft(label = label, key = key))
                showAddSectionDialog = false
            }
        )
    }

    val rowSectionIdx = addRowForSectionIndex
    if (rowSectionIdx != null) {
        AddRowDialog(
            onDismiss = { addRowForSectionIndex = null },
            onConfirm = { label, key, start, end, price ->
                sections[rowSectionIdx] = sections[rowSectionIdx].copy(
                    rows = sections[rowSectionIdx].rows + RowDto(label, key, start, end, price)
                )
                addRowForSectionIndex = null
            }
        )
    }

    val canSave = templateLabel.isNotBlank() && sections.isNotEmpty() && sections.all { it.rows.isNotEmpty() }

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
                    "Схема рассадки",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    venueSpaceLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { showAddSectionDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Добавить секцию")
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = templateLabel,
                    onValueChange = { templateLabel = it },
                    label = { Text("Название схемы") },
                    placeholder = { Text("напр. «Основная схема»") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            if (sections.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Нет секций", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Нажмите «+» чтобы добавить секцию (ВИП, Партер, Балкон…)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            itemsIndexed(sections) { sectionIdx, section ->
                SectionCard(
                    section = section,
                    onAddRow = { addRowForSectionIndex = sectionIdx },
                    onDeleteRow = { rowIdx ->
                        sections[sectionIdx] = section.copy(
                            rows = section.rows.toMutableList().also { it.removeAt(rowIdx) }
                        )
                    },
                    onDeleteSection = { sections.removeAt(sectionIdx) }
                )
            }

            if (error != null) {
                item {
                    Text(
                        "Ошибка: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    isSubmitting = true
                    error = null
                    scope.launch {
                        runCatching {
                            AppContainer.layoutTemplateService.create(
                                CreateLayoutTemplateRequest(
                                    venueSpaceId = venueSpaceId,
                                    label = templateLabel.trim(),
                                    sections = sections.map { s ->
                                        SectionDto(label = s.label, key = s.key, rows = s.rows)
                                    }
                                )
                            )
                        }.onSuccess {
                            navigator.pop()
                        }.onFailure {
                            CrashReporter.log(it)
                            error = it.message
                            isSubmitting = false
                        }
                    }
                },
                enabled = canSave && !isSubmitting,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Сохранить схему")
            }
        }
    }
}

@Composable
private fun SectionCard(
    section: SectionDraft,
    onAddRow: () -> Unit,
    onDeleteRow: (Int) -> Unit,
    onDeleteSection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                section.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            Text(
                "key: ${section.key}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(onClick = onAddRow, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.Add, contentDescription = "Добавить ряд", modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDeleteSection, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Удалить секцию",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (section.rows.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            section.rows.forEachIndexed { rowIdx, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(row.label, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        Text(
                            "места ${row.startSeat}–${row.endSeat} · ${row.price} ₽",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDeleteRow(rowIdx) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Удалить ряд",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                if (rowIdx < section.rows.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        } else {
            Text(
                "Нет рядов — нажмите «+»",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun AddSectionDialog(onDismiss: () -> Unit, onConfirm: (label: String, key: String) -> Unit) {
    var label by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var keyEditedManually by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить секцию") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = {
                        label = it
                        if (!keyEditedManually) key = it.lowercase().replace(" ", "_")
                    },
                    label = { Text("Название (напр. «ВИП», «Партер»)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it; keyEditedManually = true },
                    label = { Text("Ключ (латиница, уникален)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(label.trim(), key.trim()) },
                enabled = label.isNotBlank() && key.isNotBlank()
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun AddRowDialog(
    onDismiss: () -> Unit,
    onConfirm: (label: String, key: String, startSeat: Int, endSeat: Int, price: Int) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var keyEditedManually by remember { mutableStateOf(false) }
    var startStr by remember { mutableStateOf("1") }
    var endStr by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }

    val start = startStr.toIntOrNull() ?: 0
    val end = endStr.toIntOrNull() ?: 0
    val price = priceStr.toIntOrNull() ?: 0
    val valid = label.isNotBlank() && key.isNotBlank() && start > 0 && end >= start && price > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить ряд") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = {
                        label = it
                        if (!keyEditedManually) key = it.lowercase().replace(" ", "_")
                    },
                    label = { Text("Название ряда (напр. «Ряд 1»)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it; keyEditedManually = true },
                    label = { Text("Ключ ряда (латиница, уникален в секции)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startStr,
                        onValueChange = { if (it.all(Char::isDigit)) startStr = it },
                        label = { Text("Место с") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = endStr,
                        onValueChange = { if (it.all(Char::isDigit)) endStr = it },
                        label = { Text("Место по") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { if (it.all(Char::isDigit)) priceStr = it },
                    label = { Text("Цена (₽)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(label.trim(), key.trim(), start, end, price) },
                enabled = valid
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private data class SectionDraft(
    val label: String,
    val key: String,
    val rows: List<RowDto> = emptyList()
)

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.data.api.dto.CreateVenueApplicationRequest
import com.karrad.ticketsclient.data.api.dto.VenueApplicationDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.util.rememberFilePicker

@Composable
fun VenueApplicationScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val vm = viewModel { VenueApplicationViewModel(AppContainer.venueApplicationService) }
    val state by vm.state.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var cityLabel by remember { mutableStateOf("") }
    var subjectLabel by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var uploadingAppId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val pickFiles = rememberFilePicker { files ->
        val appId = uploadingAppId ?: return@rememberFilePicker
        if (files.isNotEmpty()) vm.uploadDocuments(appId, files)
        uploadingAppId = null
    }

    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            showForm = false
            name = ""; cityLabel = ""; subjectLabel = ""; address = ""; description = ""
            vm.clearSubmitSuccess()
        }
    }

    LaunchedEffect(state.uploadError) {
        if (state.uploadError != null) {
            snackbarHostState.showSnackbar("Ошибка загрузки: ${state.uploadError}")
            vm.clearUploadError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    "Заявки на площадки",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showForm = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = "Подать заявку")
                }
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
                state.applications.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Заявок пока нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    items(state.applications) { app ->
                        VenueApplicationCard(
                            app = app,
                            isUploading = state.isUploading && uploadingAppId == app.id,
                            onAddDocuments = {
                                uploadingAppId = app.id
                                pickFiles()
                            }
                        )
                    }
                    item { Spacer(Modifier.height(96.dp)) }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        )
    }

    if (showForm) {
        AlertDialog(
            onDismissRequest = { showForm = false },
            title = { Text("Заявка на площадку") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Название площадки *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cityLabel,
                        onValueChange = { cityLabel = it },
                        label = { Text("Город *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subjectLabel,
                        onValueChange = { subjectLabel = it },
                        label = { Text("Субъект РФ *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Адрес *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Описание") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        "Документы можно прикрепить после создания заявки.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.submit(
                            CreateVenueApplicationRequest(
                                name = name.trim(),
                                cityLabel = cityLabel.trim(),
                                subjectLabel = subjectLabel.trim(),
                                address = address.trim(),
                                description = description.trim().ifBlank { null }
                            )
                        )
                    },
                    enabled = name.isNotBlank() && cityLabel.isNotBlank() &&
                        subjectLabel.isNotBlank() && address.isNotBlank() &&
                        !state.isSubmitting
                ) {
                    Text(if (state.isSubmitting) "Отправка..." else "Отправить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForm = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun VenueApplicationCard(
    app: VenueApplicationDto,
    isUploading: Boolean,
    onAddDocuments: () -> Unit
) {
    val statusColor = when (app.status) {
        "APPROVED" -> MaterialTheme.colorScheme.primary
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }
    val statusLabel = when (app.status) {
        "APPROVED" -> "Одобрено"
        "REJECTED" -> "Отклонено"
        else -> "На рассмотрении"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelMedium,
                color = statusColor
            )
        }
        Text(
            text = "${app.cityLabel}, ${app.address}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = app.createdAt.take(10),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (app.documentUrls.isNotEmpty()) {
            Text(
                text = "Документов: ${app.documentUrls.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (app.status == "PENDING") {
            Spacer(Modifier.height(4.dp))
            if (isUploading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Text(
                        "Загрузка документов...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onAddDocuments,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Outlined.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (app.documentUrls.isEmpty()) "Прикрепить документы"
                        else "Добавить ещё документы",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

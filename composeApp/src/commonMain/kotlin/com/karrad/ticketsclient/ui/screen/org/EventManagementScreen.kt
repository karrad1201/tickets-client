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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.AttendeeDto
import com.karrad.ticketsclient.data.api.dto.EventPhotoDto
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.component.EventImage
import com.karrad.ticketsclient.ui.util.formatEventTime
import com.karrad.ticketsclient.ui.util.rememberFilePicker
import kotlinx.coroutines.launch

@Composable
fun EventManagementScreen(event: OrgEventItem) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteLoading by remember { mutableStateOf(false) }
    var photos by remember { mutableStateOf<List<EventPhotoDto>>(emptyList()) }
    var photosLoading by remember { mutableStateOf(false) }
    var attendees by remember { mutableStateOf<List<AttendeeDto>>(emptyList()) }
    var attendeesPage by remember { mutableStateOf(0) }
    var attendeesHasMore by remember { mutableStateOf(true) }
    var attendeesLoading by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val reachedBottom by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val total = listState.layoutInfo.totalItemsCount
            last != null && total > 0 && last.index >= total - 2
        }
    }

    LaunchedEffect(event.id) {
        photosLoading = true
        runCatching { photos = AppContainer.eventService.getPhotos(event.id) }
            .onFailure { CrashReporter.log(it) }
        photosLoading = false

        attendeesLoading = true
        runCatching {
            val page = AppContainer.eventService.getAttendees(event.id, 0, 20)
            attendees = page
            attendeesHasMore = page.size == 20
            attendeesPage = 1
        }.onFailure { CrashReporter.log(it) }
        attendeesLoading = false
    }

    val pickPhoto = rememberFilePicker(accept = "image/*") { files ->
        val file = files.firstOrNull() ?: return@rememberFilePicker
        scope.launch {
            photosLoading = true
            runCatching {
                val dto = AppContainer.eventService.uploadPhoto(event.id, file, photos.size)
                photos = photos + dto
            }.onFailure { CrashReporter.log(it) }
            photosLoading = false
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom && attendeesHasMore && !attendeesLoading) {
            attendeesLoading = true
            runCatching {
                val page = AppContainer.eventService.getAttendees(event.id, attendeesPage, 20)
                attendees = attendees + page
                attendeesHasMore = page.size == 20
                attendeesPage++
            }.onFailure { CrashReporter.log(it) }
            attendeesLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top bar
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
                "Мероприятие",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Шапка: название + дата + площадка
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        event.label,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        event.time.formatEventTime(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (event.venueLabel != null) {
                        Text(
                            event.venueLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Статистика продаж
            if (event.hasInventory) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.BarChart,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Статистика продаж",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${event.sold} / ${event.capacity} продано",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        if (event.capacity > 0) {
                            val pct = (event.sold * 100f / event.capacity).toInt()
                            val progress = event.sold.toFloat() / event.capacity
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                "$pct% заполнено",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Кнопки управления
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!event.hasInventory) {
                        Button(
                            onClick = {
                                navigator.push(
                                    com.karrad.ticketsclient.ui.navigation.SetupInventoryScreen(event.id, event.venueSpaceId)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Настроить билеты")
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            navigator.push(com.karrad.ticketsclient.ui.navigation.EditEventScreen(event.id))
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Редактировать")
                    }
                }
            }

            // Галерея фотографий
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Галерея",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { pickPhoto() },
                            enabled = !photosLoading
                        ) {
                            if (photosLoading)
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            else
                                Icon(
                                    Icons.Outlined.AddPhotoAlternate,
                                    contentDescription = "Добавить фото",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                        }
                    }
                    if (photos.isEmpty() && !photosLoading) {
                        Text(
                            "Фотографий пока нет",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(photos, key = { it.id }) { photo ->
                                Box {
                                    EventImage(
                                        imageUrl = photo.url,
                                        seed = photo.id,
                                        modifier = androidx.compose.ui.Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                runCatching {
                                                    AppContainer.eventService.deletePhoto(event.id, photo.id)
                                                    photos = photos.filter { it.id != photo.id }
                                                }.onFailure { CrashReporter.log(it) }
                                            }
                                        },
                                        modifier = androidx.compose.ui.Modifier
                                            .align(Alignment.TopEnd)
                                            .size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Close,
                                            contentDescription = "Удалить фото",
                                            tint = Color.White,
                                            modifier = androidx.compose.ui.Modifier
                                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                                .padding(4.dp)
                                                .size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Список посетителей
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        Icons.Outlined.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp).padding(end = 0.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Посетители",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (attendees.isEmpty() && !attendeesLoading) {
                item {
                    Text(
                        "Пока никто не купил билет",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(attendees) { attendee ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(attendee.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    if (attendee.maskedPhone != null) {
                        Text(
                            attendee.maskedPhone,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (attendeesLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            }

            item { Spacer(Modifier.height(96.dp)) }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Удалить мероприятие?") },
            text = { Text("Это действие необратимо. Все билеты и инвентарь будут удалены.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        deleteLoading = true
                        scope.launch {
                            runCatching { AppContainer.eventService.deleteEvent(event.id) }
                                .onSuccess { navigator.pop() }
                                .onFailure { CrashReporter.log(it) }
                            deleteLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    if (deleteLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    else Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Отмена") }
            }
        )
    }
}

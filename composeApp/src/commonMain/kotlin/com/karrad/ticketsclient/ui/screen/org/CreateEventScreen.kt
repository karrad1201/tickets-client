package com.karrad.ticketsclient.ui.screen.org

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.data.api.FileBytes
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.util.rememberFilePicker
import com.karrad.ticketsclient.ui.util.toImageBitmap

@Composable
fun CreateEventScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val vm = viewModel {
        CreateEventViewModel(
            AppContainer.eventService,
            AppContainer.orgMemberService,
            AppContainer.geoService,
            AppContainer.venueSpaceService
        )
    }
    val state by vm.state.collectAsState()

    var label by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedVenueId by remember { mutableStateOf("") }
    var selectedVenueLabel by remember { mutableStateOf("Выберите площадку") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedCategoryLabel by remember { mutableStateOf("Выберите категорию") }
    var selectedAgeRating by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }
    var venueMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var spaceMenuExpanded by remember { mutableStateOf(false) }
    var selectedSpaceId by remember { mutableStateOf<String?>(null) }
    var selectedSpaceLabel by remember { mutableStateOf("Выберите зал / пространство") }
    var selectedSpaceType by remember { mutableStateOf("ADMISSION") }
    var coverFile by remember { mutableStateOf<FileBytes?>(null) }
    val pickCover = rememberFilePicker { files -> coverFile = files.firstOrNull() }

    LaunchedEffect(state.createdEventId) {
        val eventId = state.createdEventId ?: return@LaunchedEffect
        navigator.replace(com.karrad.ticketsclient.ui.navigation.SetupInventoryScreen(eventId, selectedSpaceId))
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
                "Новое мероприятие",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Название") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Cover image picker
                val coverBitmap = coverFile?.bytes?.toImageBitmap()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { pickCover() },
                    contentAlignment = Alignment.Center
                ) {
                    if (coverBitmap != null) {
                        Image(
                            bitmap = coverBitmap,
                            contentDescription = "Обложка мероприятия",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Добавить обложку *",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Venue selector
                Box {
                    OutlinedButton(
                        onClick = { venueMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedVenueLabel, modifier = Modifier.weight(1f))
                    }
                    DropdownMenu(
                        expanded = venueMenuExpanded,
                        onDismissRequest = { venueMenuExpanded = false }
                    ) {
                        state.venues.forEach { venue ->
                            DropdownMenuItem(
                                text = { Text(venue.label) },
                                onClick = {
                                    selectedVenueId = venue.id
                                    selectedVenueLabel = venue.label
                                    venueMenuExpanded = false
                                    // Сбросить выбор зала и загрузить новые
                                    selectedSpaceId = null
                                    selectedSpaceLabel = "Выберите зал / пространство"
                                    selectedSpaceType = "ADMISSION"
                                    vm.onVenueSelected(venue.id)
                                }
                            )
                        }
                        if (state.venues.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Нет доступных площадок", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = { venueMenuExpanded = false }
                            )
                        }
                    }
                }

                // VenueSpace selector — показывается только если загружены залы
                if (selectedVenueId.isNotBlank() && (state.spaces.isNotEmpty() || state.spacesLoading)) {
                    Box {
                        OutlinedButton(
                            onClick = { spaceMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.spacesLoading
                        ) {
                            if (state.spacesLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp).padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text(
                                if (state.spacesLoading) "Загрузка залов..." else selectedSpaceLabel,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        DropdownMenu(
                            expanded = spaceMenuExpanded,
                            onDismissRequest = { spaceMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Без зала", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    selectedSpaceId = null
                                    selectedSpaceLabel = "Без зала"
                                    selectedSpaceType = "ADMISSION"
                                    spaceMenuExpanded = false
                                }
                            )
                            state.spaces.forEach { space ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(space.label)
                                            Text(
                                                space.type,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedSpaceId = space.id
                                        selectedSpaceLabel = space.label
                                        selectedSpaceType = space.type
                                        spaceMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Category selector
                Box {
                    OutlinedButton(
                        onClick = { categoryMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedCategoryLabel, modifier = Modifier.weight(1f))
                    }
                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        state.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategoryId = cat.id
                                    selectedCategoryLabel = cat.label
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Age rating
                AgeRatingSelector(
                    selected = selectedAgeRating,
                    onSelect = { selectedAgeRating = it }
                )

                // Date input
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Дата") },
                    placeholder = { Text("2025-12-31") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time input
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = { Text("Время (UTC)") },
                    placeholder = { Text("18:00") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.error != null) {
                    Text(
                        "Ошибка: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val canSubmit = label.isNotBlank() && description.isNotBlank() &&
                    selectedVenueId.isNotBlank() && selectedCategoryId.isNotBlank() &&
                    selectedAgeRating.isNotBlank() &&
                    dateText.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                    timeText.matches(Regex("\\d{2}:\\d{2}")) &&
                    coverFile != null &&
                    !state.isSubmitting

                Button(
                    onClick = {
                        val isoTime = "${dateText}T${timeText}:00Z"
                        vm.submit(
                            label, description, selectedVenueId, selectedCategoryId,
                            selectedAgeRating, isoTime, coverFile!!,
                            venueSpaceId = selectedSpaceId,
                            hasSeatMap = selectedSpaceType == "SEATED"
                        )
                    },
                    enabled = canSubmit,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSubmitting) CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Создать мероприятие")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private val AGE_RATINGS = listOf("0+", "6+", "12+", "16+", "18+")

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AgeRatingSelector(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Возрастное ограничение *",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AGE_RATINGS.forEach { rating ->
                val isSelected = selected == rating
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onSelect(rating) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        rating,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

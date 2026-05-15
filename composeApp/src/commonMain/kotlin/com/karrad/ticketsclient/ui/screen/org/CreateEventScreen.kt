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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.karrad.ticketsclient.data.api.dto.OrgEventItem
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.EventManagementScreen
import com.karrad.ticketsclient.ui.navigation.SetupInventoryScreen
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
    var venueMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var spaceMenuExpanded by remember { mutableStateOf(false) }
    var profileMenuExpanded by remember { mutableStateOf(false) }
    var selectedSpaceId by remember { mutableStateOf<String?>(null) }
    var selectedSpaceLabel by remember { mutableStateOf("Выберите зал / пространство") }
    var selectedPriceProfileId by remember { mutableStateOf<String?>(null) }
    var selectedPriceProfileLabel by remember { mutableStateOf("Выберите ценовой профиль") }
    // Session times: list of "YYYY-MM-DD" to "HH:MM" pairs
    val sessionDates = remember { mutableStateListOf("") }
    val sessionTimes = remember { mutableStateListOf("") }
    var coverFile by remember { mutableStateOf<FileBytes?>(null) }
    val pickCover = rememberFilePicker(accept = "image/*") { files -> coverFile = files.firstOrNull() }

    LaunchedEffect(state.createdEvent) {
        val event = state.createdEvent ?: return@LaunchedEffect
        if (selectedPriceProfileId != null) {
            navigator.replace(EventManagementScreen(
                OrgEventItem(
                    id = event.id,
                    label = event.label,
                    time = event.time,
                    venueLabel = event.venueLabel,
                    venueSpaceId = selectedSpaceId,
                    hasInventory = true
                )
            ))
        } else {
            navigator.replace(SetupInventoryScreen(event.id, selectedSpaceId))
        }
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
                                    selectedPriceProfileId = null
                                    selectedPriceProfileLabel = "Выберите ценовой профиль"
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
                                    selectedPriceProfileId = null
                                    selectedPriceProfileLabel = "Выберите ценовой профиль"
                                    spaceMenuExpanded = false
                                }
                            )
                            state.spaces.forEach { space ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(space.label)
                                            Text(
                                                if (space.type == "SEATED") "С местами" else "Партер",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedSpaceId = space.id
                                        selectedSpaceLabel = space.label
                                        selectedPriceProfileId = null
                                        selectedPriceProfileLabel = "Выберите ценовой профиль"
                                        spaceMenuExpanded = false
                                        vm.onSpaceSelected(space.id)
                                    }
                                )
                            }
                        }
                    }
                }

                // Price profile selector — показывается если выбран зал
                if (selectedSpaceId != null && (state.priceProfiles.isNotEmpty() || state.priceProfilesLoading)) {
                    Box {
                        OutlinedButton(
                            onClick = { profileMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.priceProfilesLoading
                        ) {
                            if (state.priceProfilesLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp).padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text(
                                if (state.priceProfilesLoading) "Загрузка профилей..." else selectedPriceProfileLabel,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        DropdownMenu(
                            expanded = profileMenuExpanded,
                            onDismissRequest = { profileMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Без профиля", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    selectedPriceProfileId = null
                                    selectedPriceProfileLabel = "Без профиля"
                                    profileMenuExpanded = false
                                }
                            )
                            state.priceProfiles.forEach { profile ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(profile.label)
                                            Text(
                                                if (profile.mode == "SEATED") "С местами" else "Без рассадки",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedPriceProfileId = profile.id
                                        selectedPriceProfileLabel = profile.label
                                        profileMenuExpanded = false
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

                // Session times
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Сеансы",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.weight(1f)
                    )
                    if (sessionDates.size < 10) {
                        TextButton(onClick = {
                            sessionDates.add("")
                            sessionTimes.add("")
                        }) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Добавить")
                        }
                    }
                }
                sessionDates.forEachIndexed { index, date ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { sessionDates[index] = it },
                            label = { Text("Дата ${index + 1}") },
                            placeholder = { Text("2026-06-01") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.4f)
                        )
                        OutlinedTextField(
                            value = sessionTimes[index],
                            onValueChange = { sessionTimes[index] = it },
                            label = { Text("Время (UTC)") },
                            placeholder = { Text("18:00") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        if (sessionDates.size > 1) {
                            IconButton(
                                onClick = {
                                    sessionDates.removeAt(index)
                                    sessionTimes.removeAt(index)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "Удалить сеанс",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                if (state.error != null) {
                    Text(
                        "Ошибка: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val dateRegex = Regex("\\d{4}-\\d{2}-\\d{2}")
                val timeRegex = Regex("\\d{2}:\\d{2}")
                val allSessionsValid = sessionDates.indices.all { i ->
                    sessionDates[i].matches(dateRegex) && sessionTimes[i].matches(timeRegex)
                }
                val canSubmit = label.isNotBlank() && description.isNotBlank() &&
                    selectedVenueId.isNotBlank() && selectedCategoryId.isNotBlank() &&
                    selectedAgeRating.isNotBlank() &&
                    allSessionsValid &&
                    coverFile != null &&
                    !state.isSubmitting

                Button(
                    onClick = {
                        val isoTimes = sessionDates.indices.map { i ->
                            "${sessionDates[i]}T${sessionTimes[i]}:00Z"
                        }
                        vm.submit(
                            label, description, selectedVenueId, selectedCategoryId,
                            selectedAgeRating, isoTimes, coverFile!!,
                            venueSpaceId = selectedSpaceId,
                            priceProfileId = selectedPriceProfileId
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

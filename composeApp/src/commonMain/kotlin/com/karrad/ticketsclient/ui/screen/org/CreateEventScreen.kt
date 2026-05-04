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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.karrad.ticketsclient.di.AppContainer

@Composable
fun CreateEventScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val vm = viewModel {
        CreateEventViewModel(
            AppContainer.eventService,
            AppContainer.orgMemberService,
            AppContainer.geoService
        )
    }
    val state by vm.state.collectAsState()

    var label by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedVenueId by remember { mutableStateOf("") }
    var selectedVenueLabel by remember { mutableStateOf("Выберите площадку") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedCategoryLabel by remember { mutableStateOf("Выберите категорию") }
    var dateText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }
    var venueMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) navigator.pop()
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
                    dateText.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                    timeText.matches(Regex("\\d{2}:\\d{2}")) &&
                    !state.isSubmitting

                Button(
                    onClick = {
                        val isoTime = "${dateText}T${timeText}:00Z"
                        vm.submit(label, description, selectedVenueId, selectedCategoryId, isoTime)
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

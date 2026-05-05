package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.EventDetailScreen
import com.karrad.ticketsclient.ui.navigation.SearchScreen
import com.karrad.ticketsclient.ui.screen.tickets.OfflineBanner
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun FeedScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val rootNavigator = navigator.parent ?: navigator

    val viewModel = viewModel { FeedViewModel(AppContainer.discoveryService) }
    val state by viewModel.state.collectAsState()
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var activeFilter by remember { mutableStateOf<FilterState?>(null) }
    var filteredEvents by remember { mutableStateOf<List<com.karrad.ticketsclient.data.api.dto.EventDto>?>(null) }
    var filterLoading by remember { mutableStateOf(false) }

    if (showFilters) {
        FiltersBottomSheet(
            onDismiss = { showFilters = false },
            onApply = { filter ->
                activeFilter = filter
                if (!filter.hasActiveFilters) {
                    filteredEvents = null
                    return@FiltersBottomSheet
                }
                filterLoading = true
                scope.launch {
                    val dateStr = when (filter.selectedDate) {
                        "Завтра" -> {
                            val tz = TimeZone.currentSystemDefault()
                            Clock.System.now().toLocalDateTime(tz).date
                                .plus(1, DateTimeUnit.DAY).toString()
                        }
                        "Послезавтра" -> {
                            val tz = TimeZone.currentSystemDefault()
                            Clock.System.now().toLocalDateTime(tz).date
                                .plus(2, DateTimeUnit.DAY).toString()
                        }
                        else -> null
                    }
                    val allCategories = runCatching { AppContainer.geoService.getCategories() }.getOrNull().orEmpty()
                    val categoryIds = filter.categories.mapNotNull { name ->
                        allCategories.find { it.label.equals(name, ignoreCase = true) }?.id
                    }
                    runCatching {
                        AppContainer.eventService.search(
                            query = "",
                            city = AppSession.city,
                            dateFrom = dateStr,
                            dateTo = dateStr,
                            categoryIds = categoryIds
                        )
                    }.onSuccess { results ->
                        filteredEvents = results
                    }
                    filterLoading = false
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        FeedHeader(
            onSearchClick = { rootNavigator.push(SearchScreen) },
            onFilterClick = { showFilters = true },
            onCityClick = { rootNavigator.push(com.karrad.ticketsclient.ui.navigation.CityPickerScreen) },
            hasActiveFilter = activeFilter?.hasActiveFilters == true
        )

        if (AppSession.isOffline) {
            OfflineBanner("Нет подключения · афиша недоступна")
        }

        if (activeFilter?.hasActiveFilters == true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Фильтры активны",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                androidx.compose.material3.TextButton(onClick = {
                    activeFilter = null
                    filteredEvents = null
                }) {
                    Text("Сбросить", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (filterLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (filteredEvents != null) {
            FilteredResultsList(
                events = filteredEvents!!,
                onEventClick = { rootNavigator.push(EventDetailScreen(it.id)) }
            )
        } else {
            when (val s = state) {
                is FeedState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                is FeedState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Не удалось загрузить события",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(6.dp))
                        Button(
                            onClick = { viewModel.load() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) { Text("Повторить") }
                    }
                }
                is FeedState.Success -> {
                    val selectedDay by viewModel.selectedDay.collectAsState()
                    FeedContent(
                        feed = s.feed,
                        selectedDay = selectedDay,
                        onDaySelect = { viewModel.selectDay(it) },
                        onEventClick = { event -> rootNavigator.push(EventDetailScreen(event.id)) },
                        hasMore = s.hasMore,
                        onLoadMore = { viewModel.loadMore() }
                    )
                }
            }
        }
    }
}

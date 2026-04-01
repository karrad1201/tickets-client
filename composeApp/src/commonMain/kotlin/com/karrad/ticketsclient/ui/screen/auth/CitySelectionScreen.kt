package com.karrad.ticketsclient.ui.screen.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.repository.LocalCityRepository
import com.karrad.ticketsclient.domain.model.City
import com.karrad.ticketsclient.domain.usecase.SearchCitiesUseCase
import com.karrad.ticketsclient.ui.navigation.InterestsScreen

@Composable
fun CitySelectionScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val searchCities = remember { SearchCitiesUseCase(LocalCityRepository()) }

    var query by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf<City?>(null) }

    val cities = remember(query) { searchCities(query) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(text = "Выберите город", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Выберите город, для которого будем рекомендовать события и ближайшие мероприятия",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Поиск города или региона") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(cities, key = { it.id }) { city ->
                CityCard(
                    city = city,
                    selected = selectedCity?.id == city.id,
                    onClick = { selectedCity = city }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(
            onClick = {
                selectedCity?.let { AppSession.city = it.label }
                navigator.push(InterestsScreen)
            },
            enabled = selectedCity != null,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )
        ) {
            Text("Продолжить", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun CityCard(city: City, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val subjectColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                       else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = if (selected) 1.5.dp else 1.dp, color = borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = city.label,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            Text(
                text = city.subject.label,
                style = MaterialTheme.typography.bodySmall,
                color = subjectColor
            )
        }
    }
}

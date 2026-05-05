package com.karrad.ticketsclient.ui.screen.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.data.api.dto.CityDto
import com.karrad.ticketsclient.ui.navigation.InterestsScreen

private val ELISTA = CityDto(id = "elista", label = "Элиста", subject = com.karrad.ticketsclient.data.api.dto.SubjectDto(id = "kalmykia", label = "Калмыкия"))

/**
 * @param onCitySelected если передан — вызывается вместо перехода на InterestsScreen.
 *   Используется при смене города из главной ленты.
 */
@Composable
fun CitySelectionScreen(onCitySelected: ((String) -> Unit)? = null) {
    val navigator = LocalNavigator.currentOrThrow

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(text = "Выберите город", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Сервис работает в Элисте (Республика Калмыкия)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        CityCard(city = ELISTA, selected = true, onClick = {})

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                AppSession.city = ELISTA.name
                if (onCitySelected != null) {
                    onCitySelected(ELISTA.name)
                } else {
                    navigator.push(InterestsScreen)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Продолжить", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun CityCard(city: CityDto, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val regionColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
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
                text = city.name,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            city.region?.let { region ->
                Text(
                    text = region,
                    style = MaterialTheme.typography.bodySmall,
                    color = regionColor
                )
            }
        }
    }
}

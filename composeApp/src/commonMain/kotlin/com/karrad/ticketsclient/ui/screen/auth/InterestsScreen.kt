package com.karrad.ticketsclient.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.ui.navigation.MainScreen

private data class Interest(val name: String, val emoji: String, val size: Dp)

private val interests = listOf(
    Interest("Театры",    "🎭", 88.dp),
    Interest("Кино",      "🎬", 72.dp),
    Interest("Концерты",  "🎵", 80.dp),
    Interest("Выставки",  "🖼️", 64.dp),
    Interest("Спорт",     "⚽", 76.dp),
    Interest("Фестивали", "🎪", 68.dp),
    Interest("Стендап",   "🎤", 80.dp),
    Interest("Детям",     "🎠", 64.dp),
    Interest("Лекции",    "📚", 72.dp),
    Interest("Танцы",     "💃", 68.dp),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val selected = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Выберите интересы",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Отметьте до 5 своих категорий интересов",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            interests.forEach { interest ->
                val isSelected = interest.name in selected
                InterestBubble(
                    interest = interest,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) selected.remove(interest.name)
                        else if (selected.size < 5) selected.add(interest.name)
                    }
                )
            }
        }

        Button(
            onClick = {
                AppSession.userInterests = selected.toList()
                navigator.replaceAll(MainScreen)
            },
            enabled = selected.size >= 3,
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
private fun InterestBubble(
    interest: Interest,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(interest.size)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = interest.emoji,
                fontSize = (interest.size.value * 0.42f).sp
            )
        }
        Text(
            text = interest.name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

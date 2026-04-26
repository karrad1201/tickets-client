package com.karrad.ticketsclient.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.di.AppContainer
import kotlinx.coroutines.launch

private val ALL_INTERESTS = listOf(
    "Театры", "Кино", "Концерты", "Стендап", "Выставки",
    "Спорт", "Фестивали", "Детям", "Вечеринки"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    var nameValue by remember { mutableStateOf(TextFieldValue(AppSession.userName)) }
    val name = nameValue.text
    var cityValue by remember { mutableStateOf(TextFieldValue(AppSession.city)) }
    val city = cityValue.text
    var selectedInterests by remember { mutableStateOf(AppSession.userInterests.toSet()) }
    var saving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ─── Toolbar ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigator.pop() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                "Редактировать профиль",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }

        // ─── Avatar preview ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarSquare(name = name.ifBlank { "?" }, size = 72)
        }

        // ─── Fields ──────────────────────────────────────────────────────────
        Column(Modifier.padding(horizontal = 16.dp)) {
            FieldLabel("Имя и фамилия")
            OutlinedTextField(
                value = nameValue,
                onValueChange = { nameValue = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("Телефон")
            OutlinedTextField(
                value = AppSession.userPhone,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("Город")
            OutlinedTextField(
                value = cityValue,
                onValueChange = { cityValue = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            FieldLabel("Интересы")
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ALL_INTERESTS.forEach { interest ->
                    val selected = interest in selectedInterests
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedInterests = if (selected)
                                selectedInterests - interest
                            else
                                selectedInterests + interest
                        },
                        label = { Text(interest) },
                        leadingIcon = if (selected) {
                            { Icon(Icons.Outlined.Check, null, Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ─── Save button ─────────────────────────────────────────────────
            if (saveError != null) {
                Text(
                    text = saveError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            ElevatedButton(
                onClick = {
                    saving = true
                    saveError = null
                    scope.launch {
                        try {
                            val updated = AppContainer.profileService.updateProfile(
                                fullName = name.trim().ifBlank { null },
                                interests = selectedInterests.toList()
                            )
                            AppSession.userName = updated.fullName
                            AppSession.userInterests = updated.interests
                            AppSession.userAvatarUrl = updated.avatarUrl
                            AppSession.city = city.trim().ifBlank { AppSession.city }
                            navigator.pop()
                        } catch (e: Exception) {
                            saveError = "Ошибка сохранения: ${e.message}"
                        } finally {
                            saving = false
                        }
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Сохранить", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

package com.karrad.ticketsclient.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.ui.navigation.EditProfileScreen

@Composable
fun ProfileScreen() {
    val navigator = LocalNavigator.currentOrThrow

    // читаем при каждом появлении экрана (после возврата с редактирования)
    var name by remember { mutableStateOf(AppSession.userName) }
    var phone by remember { mutableStateOf(AppSession.userPhone) }
    var city by remember { mutableStateOf(AppSession.userCity) }
    var interests by remember { mutableStateOf(AppSession.userInterests) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        name = AppSession.userName
        phone = AppSession.userPhone
        city = AppSession.userCity
        interests = AppSession.userInterests
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Профиль",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = {
                navigator.push(EditProfileScreen)
            }) {
                Icon(Icons.Outlined.Edit, contentDescription = "Редактировать")
            }
        }

        // ─── Avatar + name ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarCircle(name = name, size = 88)
            Spacer(Modifier.height(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = phone,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(20.dp))

        // ─── Info card ───────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(vertical = 4.dp)) {
                ProfileRow(icon = Icons.Outlined.Phone, label = "Телефон", value = phone)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ProfileRow(icon = Icons.Outlined.LocationOn, label = "Город", value = city)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ProfileRow(
                    icon = Icons.Outlined.Star,
                    label = "Интересы",
                    value = interests.joinToString(", ")
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ─── Settings card ───────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(vertical = 4.dp)) {
                ProfileRow(
                    icon = Icons.Outlined.Notifications,
                    label = "Уведомления",
                    value = "Включены",
                    clickable = true
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ─── Logout ──────────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(vertical = 4.dp)) {
                ProfileRow(
                    icon = Icons.AutoMirrored.Outlined.ExitToApp,
                    label = "Выйти",
                    value = "",
                    tintOverride = MaterialTheme.colorScheme.error,
                    clickable = true
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun AvatarCircle(name: String, size: Int) {
    val initials = name.trim().split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    val gradients = listOf(
        listOf(Color(0xFF6C63FF), Color(0xFF3B37CC)),
        listOf(Color(0xFF00B4D8), Color(0xFF0077B6)),
        listOf(Color(0xFF2DC653), Color(0xFF1A7A35)),
        listOf(Color(0xFFFF6B6B), Color(0xFFCC3333))
    )
    val palette = gradients[kotlin.math.abs(name.hashCode()) % gradients.size]

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.verticalGradient(palette)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size * 0.38f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileRow(
    icon: ImageVector,
    label: String,
    value: String,
    tintOverride: Color? = null,
    clickable: Boolean = false
) {
    val tint = tintOverride ?: MaterialTheme.colorScheme.primary
    Surface(
        onClick = {},
        enabled = clickable,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                if (value.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

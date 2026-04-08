package com.karrad.ticketsclient.ui.screen.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.karrad.ticketsclient.ui.navigation.LoginScreen
import com.karrad.ticketsclient.ui.navigation.AboutScreen
import com.karrad.ticketsclient.ui.navigation.EditProfileScreen
import com.karrad.ticketsclient.ui.navigation.FavoritesScreen
import com.karrad.ticketsclient.ui.navigation.InterestsScreen
import com.karrad.ticketsclient.ui.navigation.SupportScreen

@Composable
fun ProfileScreen() {
    val navigator = LocalNavigator.currentOrThrow
    // EditProfileScreen открывается поверх табов
    val rootNavigator = navigator.parent ?: navigator

    var name by remember { mutableStateOf(AppSession.userName) }
    var phone by remember { mutableStateOf(AppSession.userPhone) }

    LaunchedEffect(Unit) {
        name = AppSession.userName
        phone = AppSession.userPhone
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ─── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                "Профиль",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center)
            )
            // Кнопка выхода — оранжевый кружок справа
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        AppSession.logout()
                        rootNavigator.replaceAll(LoginScreen)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = "Выйти",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ─── Карточка пользователя ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { rootNavigator.push(EditProfileScreen) }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Квадратный аватар с инициалом
                AvatarSquare(name = name, size = 52)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = phone.maskPhone(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Меню ────────────────────────────────────────────────────────────
        MenuCard {
            MenuItem(label = "Избранное", onClick = { rootNavigator.push(FavoritesScreen) })
            MenuDivider()
            MenuItem(label = "Поддержка", onClick = { rootNavigator.push(SupportScreen) })
            MenuDivider()
            MenuItem(label = "Настройка рекомендаций", onClick = { rootNavigator.push(InterestsScreen) })
            MenuDivider()
            MenuItem(label = "О приложении", onClick = { rootNavigator.push(AboutScreen) })
        }

        // Отступ под плавающий нав-бар
        Spacer(Modifier.height(96.dp))
    }
}

// ─── Квадратный аватар с инициалом ────────────────────────────────────────────

@Composable
fun AvatarSquare(name: String, size: Int) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val gradients = listOf(
        listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)), // зелёный (как в референсе)
        listOf(Color(0xFF6C63FF), Color(0xFF3B37CC)),
        listOf(Color(0xFF00B4D8), Color(0xFF0077B6)),
        listOf(Color(0xFFFF7043), Color(0xFFE64A19))
    )
    val palette = gradients[kotlin.math.abs(name.hashCode()) % gradients.size]

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(palette)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = (size * 0.42f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── Вспомогательные компоненты ───────────────────────────────────────────────

@Composable
private fun MenuCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}

@Composable
private fun MenuItem(
    label: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    tint: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                icon, null,
                tint = tint ?: MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = tint ?: MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

private fun String.maskPhone(): String {
    // "+7 999 123 45 67" → "+7 999 *** ** 67"
    return if (length > 6) take(6) + " **** ** " + takeLast(2) else this
}

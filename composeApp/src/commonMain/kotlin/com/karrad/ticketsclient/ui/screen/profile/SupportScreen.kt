package com.karrad.ticketsclient.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun SupportScreen() {
    val navigator = LocalNavigator.currentOrThrow

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { navigator.pop() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ArrowBack, "Назад",
                    modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface)
            }
            Text("Поддержка",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center))
        }

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SupportRow(
                icon = { Icon(Icons.Outlined.Email, null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                title = "Email",
                subtitle = "support@tickets.app"
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant))
            SupportRow(
                icon = { Icon(Icons.Outlined.Phone, null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                title = "Телефон",
                subtitle = "+7 800 555-35-35 (бесплатно)"
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Время работы: пн–пт, 9:00–21:00",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun SupportRow(icon: @Composable () -> Unit, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

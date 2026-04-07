package com.karrad.ticketsclient.ui.screen.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun QrScannerView(onScanned: (String) -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "📷",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Сканирование доступно на Android",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

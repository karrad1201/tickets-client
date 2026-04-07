package com.karrad.ticketsclient.ui.screen.scanner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object ScannerTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Outlined.QrCodeScanner)
            return remember { TabOptions(index = 3u, title = "Сканер", icon = icon) }
        }

    @Composable
    override fun Content() = ScannerScreen()
}

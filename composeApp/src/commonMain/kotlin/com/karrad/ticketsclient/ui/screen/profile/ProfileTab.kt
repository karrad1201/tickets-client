package com.karrad.ticketsclient.ui.screen.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(Icons.Outlined.Person)
            return remember { TabOptions(index = 2u, title = "Профиль", icon = icon) }
        }

    @Composable
    override fun Content() = ProfileScreen()
}

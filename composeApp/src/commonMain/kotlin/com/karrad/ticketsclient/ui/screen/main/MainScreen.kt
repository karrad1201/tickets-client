package com.karrad.ticketsclient.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.karrad.ticketsclient.ui.screen.feed.FeedTab
import com.karrad.ticketsclient.ui.screen.profile.ProfileTab
import com.karrad.ticketsclient.ui.screen.tickets.TicketsTab
import com.karrad.ticketsclient.ui.theme.Background

@Composable
fun MainScreen() {
    TabNavigator(tab = FeedTab) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            // Контент таба — занимает весь экран
            CurrentTab()

            // Плавающий нав-бар поверх контента
            AppBottomBar(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun AppBottomBar(modifier: Modifier = Modifier) {
    val tabNavigator = LocalTabNavigator.current
    val tabs: List<Tab> = listOf(FeedTab, TicketsTab, ProfileTab)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color.Black.copy(alpha = 0.10f),
                    spotColor = Color.Black.copy(alpha = 0.10f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.60f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = tabNavigator.current == tab
                Box(
                    modifier = Modifier
                        .then(
                            if (selected) Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            else Modifier
                        )
                        .clickable { tabNavigator.current = tab }
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = tab.options.icon!!,
                        contentDescription = tab.options.title,
                        tint = if (selected) Color.White
                               else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

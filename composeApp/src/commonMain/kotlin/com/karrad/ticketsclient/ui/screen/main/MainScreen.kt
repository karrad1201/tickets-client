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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.screen.feed.FeedTab
import com.karrad.ticketsclient.ui.screen.profile.ProfileTab
import com.karrad.ticketsclient.ui.screen.scanner.ScannerTab
import com.karrad.ticketsclient.ui.screen.tickets.TicketsTab
import com.karrad.ticketsclient.ui.theme.Background
import kotlin.math.roundToInt

@Composable
fun MainScreen() {
    // В mock-режиме таб сканера всегда виден.
    // В prod — появляется только если пользователь состоит в организации.
    var showScanner by remember { mutableStateOf(AppContainer.isMock) }

    LaunchedEffect(Unit) {
        if (!AppContainer.isMock) {
            showScanner = try {
                AppContainer.scannerService.getMyOrgEvents(AppSession.authToken).isNotEmpty()
            } catch (e: Exception) {
                println("MainScreen: не удалось проверить доступ к сканеру — ${e.message}")
                false
            }
        }
    }

    TabNavigator(tab = FeedTab) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            // Контент таба — занимает весь экран
            CurrentTab()

            // Плавающий нав-бар поверх контента
            AppBottomBar(
                showScanner = showScanner,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun AppBottomBar(showScanner: Boolean, modifier: Modifier = Modifier) {
    val tabNavigator = LocalTabNavigator.current
    val tabs: List<Tab> = remember(showScanner) {
        if (showScanner) listOf(FeedTab, TicketsTab, ScannerTab, ProfileTab)
        else listOf(FeedTab, TicketsTab, ProfileTab)
    }
    val selectedIndex = tabs.indexOfFirst { tabNavigator.current == it }.coerceAtLeast(0)

    val density = LocalDensity.current
    val gap = 8.dp
    val gapPx = with(density) { gap.toPx() }

    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "pillSlide"
    )

    var tabSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Скользящая пилюля — рисуется ДО иконок, чтобы быть позади
            if (tabSize != IntSize.Zero) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (animatedIndex * (tabSize.width + gapPx)).roundToInt(),
                                y = 0
                            )
                        }
                        .size(
                            width = with(density) { tabSize.width.toDp() },
                            height = with(density) { tabSize.height.toDp() }
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                )
            }

            // Иконки — рисуются поверх пилюли
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedIndex == index
                    val iconTint by animateColorAsState(
                        targetValue = if (selected) Color.White
                                      else MaterialTheme.colorScheme.onBackground,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "tabTint"
                    )

                    Box(
                        modifier = Modifier
                            .onSizeChanged { if (index == 0) tabSize = it }
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { tabNavigator.current = tab }
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = tab.options.icon!!,
                            contentDescription = tab.options.title,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

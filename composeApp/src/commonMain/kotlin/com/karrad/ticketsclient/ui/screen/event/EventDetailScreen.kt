package com.karrad.ticketsclient.ui.screen.event

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.karrad.ticketsclient.AppSession
import com.karrad.ticketsclient.crash.CrashReporter
import com.karrad.ticketsclient.data.api.dto.EventDto
import com.karrad.ticketsclient.data.api.dto.EventPhotoDto
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.navigation.SeatMapScreen
import com.karrad.ticketsclient.ui.navigation.TicketTypeScreen
import com.karrad.ticketsclient.ui.component.EventImage
import com.karrad.ticketsclient.ui.screen.feed.EventImagePlaceholder
import com.karrad.ticketsclient.ui.theme.FreeGreen
import com.karrad.ticketsclient.ui.util.formatEventDate
import com.karrad.ticketsclient.ui.util.formatSessionsCompact
import com.karrad.ticketsclient.ui.util.formatPrice
import com.karrad.ticketsclient.ui.util.rememberShareLauncher
import androidx.compose.material.icons.outlined.Share
import kotlinx.coroutines.launch

@Composable
fun EventDetailScreen(eventId: String) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    var loadedEvent by remember { mutableStateOf<EventDto?>(null) }
    var isFavorite by remember { mutableStateOf(AppSession.isFavorite(eventId)) }
    val favoriteColor by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFE53935) else Color.Black,
        animationSpec = tween(200),
        label = "favColor"
    )

    var photos by remember { mutableStateOf<List<EventPhotoDto>>(emptyList()) }
    var galleryIndex by remember { mutableIntStateOf(0) }
    var showGallery by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        loadedEvent = try { AppContainer.eventService.getEvent(eventId) } catch (e: Exception) { CrashReporter.log(e); null }
        photos = try { AppContainer.eventService.getPhotos(eventId) } catch (e: Exception) { CrashReporter.log(e); emptyList() }
    }

    val event = loadedEvent ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val scrollState = rememberScrollState()
    val parallaxOffset = scrollState.value * 0.4f

    val shareText = buildString {
        append("«${event.label}»")
        event.time.formatEventDate()?.let { append(" — $it") }
        (event.venueLabel ?: event.venueId).let { append(", $it") }
    }
    val share = rememberShareLauncher(shareText)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ─── Hero ────────────────────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                EventImage(
                    imageUrl = event.imageUrl,
                    seed = event.id,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { translationY = parallaxOffset }
                )

                // тёмный градиент снизу
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                startY = 100f
                            )
                        )
                )

                // Кнопки: назад (слева) + избранное (справа)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                                .clickable { share() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = "Поделиться",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                                .clickable {
                                    val newFavorite = !isFavorite
                                    isFavorite = newFavorite
                                    AppSession.toggleFavorite(eventId, newFavorite)
                                    scope.launch {
                                        runCatching {
                                            if (newFavorite) {
                                                AppContainer.favoriteService.add(eventId)
                                            } else {
                                                AppContainer.favoriteService.remove(eventId)
                                            }
                                        }.onFailure {
                                            CrashReporter.log(it)
                                            isFavorite = !newFavorite
                                            AppSession.toggleFavorite(eventId, !newFavorite)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                                tint = favoriteColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Время/дата — снизу слева на шапке
                val timeLabel = if (event.sessionTimes.size > 1)
                    formatSessionsCompact(event.sessionTimes)
                else
                    event.time.formatEventDate() ?: event.time
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.52f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = timeLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            // ─── Контент ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // Теги: возраст + категория + площадка
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    event.ageRating?.let { TagChip(text = it, outlined = true) }
                    event.categoryLabel?.let { TagChip(text = it, outlined = true) }
                    TagChip(text = event.venueLabel ?: event.venueId, filled = true)
                }

                Spacer(Modifier.height(12.dp))

                // Название
                Text(
                    text = event.label,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                // Сеансы (если несколько)
                if (event.sessionTimes.size > 1) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Сеансы",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        event.sessionTimes.forEachIndexed { index, sessionTime ->
                            val sessionId = event.sessionEventIds.getOrNull(index)
                            val isCurrentSession = sessionId == event.id || (sessionId == null && sessionTime == event.time)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isCurrentSession) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .then(
                                        if (!isCurrentSession && sessionId != null)
                                            Modifier.clickable { navigator.push(com.karrad.ticketsclient.ui.navigation.EventDetailScreen(sessionId)) }
                                        else Modifier
                                    )
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = sessionTime.formatEventDate() ?: sessionTime,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isCurrentSession) Color.White
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                // О мероприятии
                Text(
                    "О мероприятии",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                ExpandableText(text = event.description)

                // Галерея (если есть фото)
                if (photos.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Галерея",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(photos) { index, photo ->
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        galleryIndex = index
                                        showGallery = true
                                    }
                            ) {
                                EventImage(
                                    imageUrl = photo.url,
                                    seed = photo.id,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                // Место проведения
                Text(
                    "Место проведения",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = event.venueLabel ?: event.venueId,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(100.dp))
            }
        }

        // ─── Sticky CTA ───────────────────────────────────────────────────────
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            if (event.minPrice != null) {
                val isFree = event.minPrice == 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isFree) FreeGreen else MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (event.hasSeatMap) {
                                navigator.push(SeatMapScreen(event.id, event.sessionEventIds, event.sessionTimes))
                            } else {
                                navigator.push(TicketTypeScreen(event.id, event.sessionEventIds, event.sessionTimes))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isFree) "Получить бесплатно" else "Выбрать",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        if (!isFree) {
                            Text(
                                "от ${event.minPrice.formatPrice()} ₽",
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Билеты ещё не поступили в продажу",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // ─── Галерея fullscreen ───────────────────────────────────────────────────
    if (showGallery && photos.isNotEmpty()) {
        val pagerState = rememberPagerState(initialPage = galleryIndex) { photos.size }
        Dialog(
            onDismissRequest = { showGallery = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EventImage(
                            imageUrl = photos[page].url,
                            seed = photos[page].id,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                IconButton(
                    onClick = { showGallery = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Закрыть", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Text(
                    "${pagerState.currentPage + 1} / ${photos.size}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun TagChip(
    text: String,
    outlined: Boolean = false,
    filled: Boolean = false
) {
    val bg = when {
        filled   -> MaterialTheme.colorScheme.primary
        outlined -> Color.Transparent
        else     -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        filled   -> Color.White
        outlined -> MaterialTheme.colorScheme.onSurfaceVariant
        else     -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderMod = if (outlined)
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    else
        Modifier.clip(RoundedCornerShape(20.dp)).background(bg)

    Box(
        modifier = borderMod.padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = textColor)
    }
}

@Composable
private fun ExpandableText(text: String, collapsedLines: Int = 4) {
    var expanded by remember { mutableStateOf(false) }
    val isLong = text.length > 180

    Column(Modifier.animateContentSize()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedLines,
            overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
        )
        if (isLong) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (expanded) "Свернуть" else "Читать полностью",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}


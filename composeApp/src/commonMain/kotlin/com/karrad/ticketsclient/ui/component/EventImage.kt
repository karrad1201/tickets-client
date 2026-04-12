package com.karrad.ticketsclient.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.karrad.ticketsclient.data.cache.CachedImageLoader
import com.karrad.ticketsclient.data.cache.ImageCache
import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.ui.screen.feed.EventImagePlaceholder
import com.karrad.ticketsclient.ui.util.toImageBitmap

/**
 * Показывает изображение события по URL с кешированием.
 * При отсутствии URL или ошибке загрузки показывает [EventImagePlaceholder].
 */
@Composable
fun EventImage(imageUrl: String?, seed: String, modifier: Modifier = Modifier) {
    var bitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        if (!imageUrl.isNullOrBlank()) {
            val bytes = CachedImageLoader.load(AppContainer.httpClient, imageUrl, ImageCache.EVENT_IMAGE_TTL_MS)
            bitmap = bytes?.toImageBitmap()
        }
    }

    val b = bitmap
    if (b != null) {
        Image(
            bitmap = b,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        EventImagePlaceholder(seed = seed, modifier = modifier)
    }
}

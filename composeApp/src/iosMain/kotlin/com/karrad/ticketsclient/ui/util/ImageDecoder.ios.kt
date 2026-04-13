package com.karrad.ticketsclient.ui.util

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.skia.Image

actual fun ByteArray.toImageBitmap(): ImageBitmap? = runCatching {
    Image.makeFromEncoded(this).toComposeImageBitmap()
}.getOrNull()

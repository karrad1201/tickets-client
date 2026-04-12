package com.karrad.ticketsclient.ui.util

import androidx.compose.ui.graphics.ImageBitmap

/** Декодирует байты изображения в [ImageBitmap]. Возвращает null при ошибке. */
expect fun ByteArray.toImageBitmap(): ImageBitmap?

package com.karrad.ticketsclient.ui.util

import androidx.compose.runtime.Composable
import com.karrad.ticketsclient.data.api.FileBytes

@Composable
expect fun rememberFilePicker(
    accept: String = "*/*",
    onFiles: (List<FileBytes>) -> Unit
): () -> Unit

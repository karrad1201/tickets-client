package com.karrad.ticketsclient.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberShareLauncher(text: String): () -> Unit

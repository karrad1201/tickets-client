package com.karrad.ticketsclient.ui.screen.scanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun QrScannerView(onScanned: (String) -> Unit, modifier: Modifier = Modifier)

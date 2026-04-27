package com.karrad.ticketsclient.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun EventImagePlaceholder(seed: String, modifier: Modifier = Modifier) {
    val palettes = listOf(
        listOf(Color(0xFF1A1A2E), Color(0xFF16213E)),
        listOf(Color(0xFF0F3460), Color(0xFF533483)),
        listOf(Color(0xFF2D6A4F), Color(0xFF1B4332)),
        listOf(Color(0xFF6A0572), Color(0xFF3A0CA3)),
        listOf(Color(0xFF7B2D00), Color(0xFF3E1200)),
        listOf(Color(0xFF023E8A), Color(0xFF0077B6)),
        listOf(Color(0xFF1D3557), Color(0xFF457B9D)),
        listOf(Color(0xFF3D0000), Color(0xFF6B0000))
    )
    val palette = palettes[kotlin.math.abs(seed.hashCode()) % palettes.size]
    Box(modifier = modifier.background(Brush.verticalGradient(palette)))
}

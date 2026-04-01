package com.karrad.ticketsclient.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary                = Primary,
    onPrimary              = OnPrimary,
    primaryContainer       = TagBackground,
    onPrimaryContainer     = Primary,
    background             = Background,
    onBackground           = TextPrimary,
    surface                = Surface,
    onSurface              = TextPrimary,
    surfaceVariant         = SurfaceVariant,
    onSurfaceVariant       = TextSecondary,
    outline                = Divider,
    outlineVariant         = Divider,
    error                  = Color(0xFFFF3B30)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = AppTypography,
        content     = content
    )
}

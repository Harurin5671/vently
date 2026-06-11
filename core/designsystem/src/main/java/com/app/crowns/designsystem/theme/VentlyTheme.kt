package com.app.crowns.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.app.crowns.designsystem.color.VentlyDarkColorScheme
import com.app.crowns.designsystem.color.VentlyLightColorScheme
import com.app.crowns.designsystem.shape.VentlyShapes
import com.app.crowns.designsystem.typography.VentlyTypography

@Composable
fun VentlyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) VentlyDarkColorScheme else VentlyLightColorScheme,
        shapes = VentlyShapes,
        typography = VentlyTypography,
        content = content,
    )
}

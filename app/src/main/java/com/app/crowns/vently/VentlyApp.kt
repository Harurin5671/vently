package com.app.crowns.vently

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.rememberNavBackStack
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.vently.navigation.AppDestination
import com.app.crowns.vently.navigation.AppNavHost

@Composable
fun VentlyApp() {
    val backStack = rememberNavBackStack(AppDestination.Splash)

    VentlyTheme {
        AppNavHost(backStack = backStack)
    }
}

@ThemePreviews
@Composable
private fun VentlyAppPreview() {
    VentlyTheme {
        VentlyApp()
    }
}
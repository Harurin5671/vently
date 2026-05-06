package com.app.crowns.vently

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.vently.splash.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun VentlyApp() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen()
        LaunchedEffect(Unit) {
            delay(2000) // 2 segundos de splash
            showSplash = false
        }
    } else {
        Scaffold(
            modifier = Modifier
                .systemBarsPadding()
        ) { innerPadding ->
            Text(
                text = "Prueba",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@ThemePreviews
@Composable
private fun VentlyAppPreview() {
    VentlyTheme {
        VentlyApp()
    }
}
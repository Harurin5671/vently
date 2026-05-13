package com.app.crowns.vently.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.vently.R

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isSessionActive by viewModel.isSessionActive.collectAsStateWithLifecycle()

    LaunchedEffect(isSessionActive) {
        isSessionActive?.let { active ->
            if (active) onNavigateToHome() else onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = OnSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Facturación Electrónica Simple",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SplashScreenPreview() {
    VentlyTheme {
        SplashScreen(
            onNavigateToAuth = {},
            onNavigateToHome = {}
        )
    }
}
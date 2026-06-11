package com.app.crowns.onboarding.ui.pages

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.onboarding.ui.components.BusinessSummaryCard
import com.app.crowns.onboarding.ui.components.DecorativeWaveBar
import com.app.crowns.onboarding.ui.components.SuccessHeroSection
import com.app.crowns.onboarding.ui.components.VentlyPrimaryButton
import com.app.crowns.ui.components.VentlyInfoBanner

// ─────────────────────────────────────────────
// SCREEN ROOT
// ─────────────────────────────────────────────

@Composable
fun OnboardingSuccessScreen(
    businessName: String,
    ruc: String,
    address: String,
    currentStep: Int,
    totalSteps: Int,
    logoUri: Uri?,
    isFinishing: Boolean,
    error: String?,
    onNavigateToApp: () -> Unit,
    onLogoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { VentlyTopBar() },
        bottomBar = {
            Column {
                VentlyInfoBanner(
                    message = error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                VentlyPrimaryButton(
                    label = "Ir a Vently",
                    onClick = onNavigateToApp,
                    enabled = !isFinishing,
                    isLoading = isFinishing,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                )
            }
        },
        containerColor = Surface,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(12.dp))

            OnboardingProgressBar(
                currentStep = currentStep,
                totalSteps = totalSteps,
            )

            Spacer(Modifier.height(32.dp))

            SuccessHeroSection(
                businessName = businessName,
                logoUri = logoUri,
                onLogoSelected = onLogoSelected
            )

            Spacer(Modifier.height(32.dp))

            BusinessSummaryCard(
                businessName = businessName,
                ruc = ruc,
                address = address,
                logoUri = logoUri,
                onLogoSelected = onLogoSelected,
            )

            Spacer(Modifier.weight(1f))

            DecorativeWaveBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .padding(vertical = 16.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────
// TOP APP BAR
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VentlyTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Vently",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceLowest,
        ),
    )
}

// ─────────────────────────────────────────────
// PROGRESS BAR
// ─────────────────────────────────────────────

@Composable
fun OnboardingProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            repeat(totalSteps) { index ->
                val filled = index < currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) Success
                            else SurfaceVariant
                        ),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Paso $currentStep de $totalSteps",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextVeryMuted,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
        )
    }
}

// ─────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingSuccessScreenPreview() {
    MaterialTheme {
        OnboardingSuccessScreen(
            businessName = "Café Aroma S.A.C.",
            ruc = "20608453210",
            address = "Av. Benavides 1234, Miraflores",
            currentStep = 2,
            totalSteps = 2,
            logoUri = null,
            isFinishing = false,
            error = null,
            onNavigateToApp = {},
            onLogoSelected = {},
        )
    }
}
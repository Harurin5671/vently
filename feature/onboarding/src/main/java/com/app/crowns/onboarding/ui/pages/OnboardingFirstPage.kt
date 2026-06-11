package com.app.crowns.onboarding.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.R
import com.app.crowns.designsystem.color.*
import com.app.crowns.onboarding.ui.components.InfoCard
import com.app.crowns.onboarding.ui.components.RucInputSection
import com.app.crowns.onboarding.ui.components.StepProgressIndicator
import com.app.crowns.onboarding.ui.components.VincularRucBottomBar
import com.app.crowns.onboarding.ui.components.VincularRucTopBar

// ─────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────
@Composable
fun OnboardingFirstPage(
    ruc: String,
    onChangedRuc: (String) -> Unit,
    isValid: Boolean,
    isSearching: Boolean = false,          // ← nuevo
    searchError: String? = null,
    businessName: String = "Ferretería López",
    currentStep: Int = 1,
    totalSteps: Int = 2,
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            VincularRucTopBar(onBack = onBack)
        },
        bottomBar = {
            VincularRucBottomBar(onContinue = onContinue, isValid = isValid, isSearching)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Indicador de progreso ──
            StepProgressIndicator(
                currentStep = currentStep,
                totalSteps = totalSteps
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Header ──
            Text(
                text = "Vincula tu RUC",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu RUC conecta tus comprobantes con SUNAT",
                fontSize = 14.sp,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Card informativa ──
            InfoCard()

            Spacer(modifier = Modifier.height(24.dp))

            // ── Input RUC ──
            RucInputSection(
                ruc = ruc,
                businessName = businessName,
                onChanged = onChangedRuc,
                isValid = isValid,
                isSearching = isSearching,
                searchError = searchError,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Ilustración ──
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Reemplaza R.drawable.sunat_illustration con tu recurso
                // Si no tienes imagen, elimina este bloque o usa un Placeholder

                Image(
                    painter = painterResource(id = R.drawable.vently_logo_icon),
                    contentDescription = "Representación visual de la conexión con SUNAT",
                    modifier = Modifier
                        .size(128.dp)
                        .graphicsLayer(alpha = 0.8f),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390)
@Composable
fun VincularRucScreenPreview() {
    MaterialTheme {
        OnboardingFirstPage(
            ruc = "2131",
            isValid = false,
            onChangedRuc = {},
        )
    }
}
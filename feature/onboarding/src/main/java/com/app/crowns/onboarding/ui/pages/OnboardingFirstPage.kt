package com.app.crowns.onboarding.ui.pages

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*

// ─────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────
@Composable
fun VincularRucScreen(
    ruc: String = "20601234567",
    businessName: String = "Ferretería López",
    currentStep: Int = 2,
    totalSteps: Int = 3,
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            VincularRucTopBar(onBack = onBack)
        },
        bottomBar = {
            VincularRucBottomBar(onContinue = onContinue)
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
            RucInputSection(ruc = ruc, businessName = businessName)

            Spacer(modifier = Modifier.height(32.dp))

            // ── Ilustración ──
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Reemplaza R.drawable.sunat_illustration con tu recurso
                // Si no tienes imagen, elimina este bloque o usa un Placeholder
                /*
                Image(
                    painter = painterResource(id = R.drawable.sunat_illustration),
                    contentDescription = "Representación visual de la conexión con SUNAT",
                    modifier = Modifier
                        .size(128.dp)
                        .graphicsLayer(alpha = 0.8f),
                    contentScale = ContentScale.Fit
                )
                */

                // Placeholder mientras no tengas el drawable
                SunatIllustrationPlaceholder()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
//  Top App Bar
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VincularRucTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = OnSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceContainerLowest
        )
    )
}

// ─────────────────────────────────────────────
//  Bottom Bar con botón "Continuar"
// ─────────────────────────────────────────────
@Composable
private fun VincularRucBottomBar(onContinue: () -> Unit) {
    Surface(
        color = Surface.copy(alpha = 0.9f),
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimary
                )
            ) {
                Text(
                    text = "Continuar",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Indicador de pasos
// ─────────────────────────────────────────────
@Composable
private fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(totalSteps) { index ->
                val isCompleted = index < currentStep
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isCompleted) PrimaryContainer else OutlineVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Paso $currentStep de $totalSteps",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Outline
        )
    }
}

// ─────────────────────────────────────────────
//  Card informativa
// ─────────────────────────────────────────────
@Composable
private fun InfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryFixed.copy(alpha = 0.2f))
            .border(
                width = 3.dp,
                color = PrimaryContainer,
                // Solo borde izquierdo: usamos un Box con padding
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            // Reemplaza con Icons.Filled.Info si no tienes el outlined
            imageVector = Icons.AutoMirrored.Filled.ArrowForward, // placeholder visual
            contentDescription = null,
            tint = PrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Necesitas tu RUC de 11 dígitos y tu clave SOL para emitir comprobantes electrónicos.",
            fontSize = 14.sp,
            color = OnPrimaryFixed,
            lineHeight = 20.sp
        )
    }
}

// ─────────────────────────────────────────────
//  Sección input RUC
// ─────────────────────────────────────────────
@Composable
private fun RucInputSection(
    ruc: String,
    businessName: String
) {
    Column {
        // Label + contador
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "RUC",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
            Text(
                text = "${ruc.length}/11",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Campo RUC (solo lectura / display)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .border(
                    width = 1.5.dp,
                    color = PrimaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ruc,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Ícono de check a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Usando un ícono de Material estándar como placeholder para check_circle
                Icon(
                    // En tu proyecto usa Icons.Filled.CheckCircle
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "RUC válido",
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Feedback de validación
        Text(
            text = "✓  RUC válido — $businessName",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Success
        )
    }
}

// ─────────────────────────────────────────────
//  Placeholder para la ilustración
// ─────────────────────────────────────────────
@Composable
private fun SunatIllustrationPlaceholder() {
    Box(
        modifier = Modifier
            .size(128.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryFixed.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🏛️",
            fontSize = 48.sp
        )
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390)
@Composable
fun VincularRucScreenPreview() {
    MaterialTheme {
        VincularRucScreen()
    }
}
package com.app.crowns.onboarding.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.R
import com.app.crowns.designsystem.color.*
import com.app.crowns.ui.components.VentlyInfoBanner

// ─────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────
@Composable
fun VincularRucScreen(
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
private fun VincularRucBottomBar(onContinue: () -> Unit, isValid: Boolean, isSearching: Boolean) {
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
                enabled = isValid && !isSearching,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryContainer,
                    contentColor = OnPrimary
                )
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = OnPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
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
            text = "Necesitas tu RUC de 11 dígitos para emitir comprobantes electrónicos.",
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
    businessName: String,
    onChanged: (String) -> Unit,
    isValid: Boolean,
    isSearching: Boolean,
    searchError: String? = null,
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .border(
                    width = 1.5.dp,
                    color = when {
                        searchError != null -> Error
                        isValid -> Success
                        else -> OutlineVariant
                    },
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = ruc,
                onValueChange = { if (it.length <= 11) onChanged(it) },
                enabled = !isSearching,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Spacer izquierdo para balancear el ícono derecho
                        Spacer(modifier = Modifier.size(24.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }

                        // Ícono de validación o progreso
                        Box(modifier = Modifier.size(24.dp)) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PrimaryContainer,
                                    strokeWidth = 2.dp
                                )
                            } else if (isValid) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "RUC válido",
                                    tint = Success,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        if (isSearching) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Buscando información...",
                fontSize = 12.sp,
                color = PrimaryContainer
            )
        } else if (searchError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            VentlyInfoBanner(message = searchError)
        } else if (isValid && businessName.isNotEmpty()) {
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
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390)
@Composable
fun VincularRucScreenPreview() {
    MaterialTheme {
        VincularRucScreen(
            ruc = "2131",
            isValid = false,
            onChangedRuc = {},
        )
    }
}
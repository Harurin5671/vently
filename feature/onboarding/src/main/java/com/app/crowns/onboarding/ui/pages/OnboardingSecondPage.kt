package com.app.crowns.onboarding.ui.pages

import android.net.Uri
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.SecondaryContainer
import com.app.crowns.designsystem.color.Success
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.designsystem.color.SurfaceLowest
import com.app.crowns.designsystem.color.SurfaceVariant
import com.app.crowns.designsystem.color.TextMuted
import com.app.crowns.designsystem.color.TextVeryMuted
import com.app.crowns.ui.components.VentlyImageSelector

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
    onNavigateToApp: () -> Unit,
    onLogoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { VentlyTopBar() },
        bottomBar = {
            VentlyPrimaryButton(
                label = "Ir a Vently",
                onClick = onNavigateToApp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            )
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
// SUCCESS HERO SECTION
// ─────────────────────────────────────────────

@Composable
fun SuccessHeroSection(
    businessName: String,
    logoUri: Uri?,
    onLogoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pulse animation on the check icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "iconPulse",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            VentlyImageSelector(
                selectedImageUri = logoUri,
                onImageSelected = onLogoSelected,
                size = 80.dp,
                modifier = Modifier.scale(scale)
            )

            // Small check badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .background(Success, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "¡Todo listo, $businessName!",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Ya puedes emitir boletas y facturas electrónicas validadas por SUNAT",
            fontSize = 15.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.widthIn(max = 280.dp),
        )
    }
}

// ─────────────────────────────────────────────
// BUSINESS SUMMARY CARD
// ─────────────────────────────────────────────

@Composable
fun BusinessSummaryCard(
    businessName: String,
    ruc: String,
    address: String,
    logoUri: Uri?,
    onLogoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(OutlineVariant, OutlineVariant)
            ),
            width = 1.dp,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header row: business name + photo selector
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SummaryLabel(text = "Negocio")
                    Text(
                        text = businessName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                    )
                }

                VentlyImageSelector(
                    selectedImageUri = logoUri,
                    onImageSelected = onLogoSelected,
                    size = 56.dp,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Divider
            HorizontalDivider(color = SurfaceVariant, thickness = 1.dp)

            // RUC + Dirección grid
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    SummaryLabel(text = "RUC")
                    Text(
                        text = ruc,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    SummaryLabel(text = "Dirección")
                    Text(
                        text = address,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface,
                    )
                }
            }
        }
    }
}

/**
 * Small uppercase label used inside cards (e.g. "RUC", "NEGOCIO")
 */
@Composable
private fun SummaryLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Outline,
        letterSpacing = 0.8.sp,
    )
}

// ─────────────────────────────────────────────
// DECORATIVE WAVE BAR
// ─────────────────────────────────────────────

@Composable
fun DecorativeWaveBar(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        // Blurred ambient blobs (purely decorative)
        Box(
            modifier = Modifier
                .size(192.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-40).dp)
                .blur(40.dp)
                .background(
                    PrimaryContainer.copy(alpha = 0.05f),
                    CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(192.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 40.dp)
                .blur(40.dp)
                .background(
                    SecondaryContainer.copy(alpha = 0.1f),
                    CircleShape,
                ),
        )

        // Animated bars
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedBar(height = 32.dp, alpha = 0.2f, delayMs = 0)
            AnimatedBar(height = 48.dp, alpha = 0.4f, delayMs = 150)
            AnimatedBar(height = 24.dp, alpha = 0.2f, delayMs = 300)
            AnimatedBar(height = 40.dp, alpha = 0.3f, delayMs = 450)
            AnimatedBar(height = 20.dp, alpha = 0.15f, delayMs = 600)
        }
    }
}

@Composable
private fun AnimatedBar(
    height: androidx.compose.ui.unit.Dp,
    alpha: Float,
    delayMs: Int,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bar_$delayMs")
    val animatedHeight by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                delayMillis = delayMs,
                easing = EaseInOutSine,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "barHeight_$delayMs",
    )

    Box(
        modifier = Modifier
            .width(6.dp)
            .height(height * animatedHeight)
            .clip(CircleShape)
            .background(Primary.copy(alpha = alpha)),
    )
}

// ─────────────────────────────────────────────
// PRIMARY BUTTON
// ─────────────────────────────────────────────

@Composable
fun VentlyPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryContainer,
            contentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
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
            onNavigateToApp = {},
            onLogoSelected = {},
        )
    }
}
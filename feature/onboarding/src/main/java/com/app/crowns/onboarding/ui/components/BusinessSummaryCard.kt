package com.app.crowns.onboarding.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.ui.components.VentlyImageSelector

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
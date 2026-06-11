package com.app.crowns.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.ui.components.shimmerEffect

@Composable
fun SettingsShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        BusinessProfileShimmer()
        
        // Facturación Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            ) {
                repeat(4) {
                    SettingsItemShimmer()
                }
            }
        }

        // Cuenta Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            ) {
                SettingsItemShimmer()
            }
        }
    }
}

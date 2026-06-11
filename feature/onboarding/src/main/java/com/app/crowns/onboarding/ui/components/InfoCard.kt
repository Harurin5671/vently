package com.app.crowns.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
@Composable
fun InfoCard() {
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
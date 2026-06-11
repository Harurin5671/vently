package com.app.crowns.history.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.Outline

@Composable
fun BreakdownRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontSize = 12.sp, color = Outline)
        Text(
            text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", amount)}",
            fontSize = 12.sp,
            color = Outline,
        )
    }
}
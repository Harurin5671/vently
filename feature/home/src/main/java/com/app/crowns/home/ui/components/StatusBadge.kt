package com.app.crowns.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
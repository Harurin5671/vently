package com.app.crowns.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OutlineVariant,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)),
            content = content,
        )
    }
}
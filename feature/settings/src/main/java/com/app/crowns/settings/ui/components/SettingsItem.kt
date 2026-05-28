package com.app.crowns.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                    )
                }
            }
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = Outline,
                modifier = Modifier.size(20.dp),
            )
        }
        if (showDivider) {
            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
        }
    }
}
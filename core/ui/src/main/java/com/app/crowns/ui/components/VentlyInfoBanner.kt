package com.app.crowns.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.Error
import com.app.crowns.designsystem.color.ErrorContainer
import com.app.crowns.designsystem.color.OnErrorContainer
import com.app.crowns.designsystem.color.OnPrimaryContainer
import com.app.crowns.designsystem.color.PrimaryContainer

enum class BannerType {
    ERROR, INFO
}

@Composable
fun VentlyInfoBanner(
    message: String?,
    modifier: Modifier = Modifier,
    type: BannerType = BannerType.ERROR,
) {
    AnimatedVisibility(
        visible = !message.isNullOrEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        val backgroundColor = when (type) {
            BannerType.ERROR -> ErrorContainer.copy(alpha = 0.7f)
            BannerType.INFO -> PrimaryContainer.copy(alpha = 0.1f)
        }
        val contentColor = when (type) {
            BannerType.ERROR -> OnErrorContainer
            BannerType.INFO -> OnPrimaryContainer
        }
        val borderColor = when (type) {
            BannerType.ERROR -> Error.copy(alpha = 0.2f)
            BannerType.INFO -> PrimaryContainer.copy(alpha = 0.2f)
        }
        val icon = when (type) {
            BannerType.ERROR -> Icons.Default.ErrorOutline
            BannerType.INFO -> Icons.Default.Info
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message ?: "",
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )
        }
    }
}

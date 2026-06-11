package com.app.crowns.onboarding.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.Primary

@Composable
fun AnimatedBar(
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

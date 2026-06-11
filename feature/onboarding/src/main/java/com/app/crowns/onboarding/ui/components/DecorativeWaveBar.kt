package com.app.crowns.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.*

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
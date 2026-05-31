package com.app.crowns.history.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme

@Composable
fun HistoryEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "📄",
                fontSize = 48.sp,
            )
            Text(
                text = "No hay comprobantes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Text(
                text = "Los comprobantes emitidos aparecerán aquí",
                fontSize = 14.sp,
                color = Outline,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun HistoryEmptyStatePreview() {
    VentlyTheme {
        HistoryEmptyState()
    }
}

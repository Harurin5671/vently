package com.app.crowns.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.Error
import com.app.crowns.designsystem.color.ErrorContainer
import com.app.crowns.designsystem.color.OnErrorContainer
import com.app.crowns.designsystem.color.OnPrimaryContainer
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryFixed
import com.app.crowns.designsystem.color.Success
import com.app.crowns.designsystem.color.SuccessBg

enum class VentlySnackbarType { ERROR, SUCCESS, INFO }

class VentlySnackbarState {
    val hostState = SnackbarHostState()
    var type by mutableStateOf(VentlySnackbarType.ERROR)
        private set

    suspend fun show(message: String, type: VentlySnackbarType = VentlySnackbarType.ERROR) {
        this.type = type
        hostState.showSnackbar(message)
    }
}

@Composable
fun rememberVentlySnackbarState(): VentlySnackbarState = remember { VentlySnackbarState() }

@Composable
fun VentlySnackbarHost(
    state: VentlySnackbarState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = state.hostState,
        modifier = modifier,
        snackbar = { data ->
            VentlySnackbarItem(data = data, type = state.type)
        }
    )
}

@Composable
private fun VentlySnackbarItem(
    data: SnackbarData,
    type: VentlySnackbarType,
    modifier: Modifier = Modifier,
) {
    val accentColor: Color
    val bgColor: Color
    val textColor: Color
    val icon: ImageVector

    when (type) {
        VentlySnackbarType.ERROR -> {
            accentColor = Error
            bgColor = ErrorContainer.copy(alpha = 0.97f)
            textColor = OnErrorContainer
            icon = Icons.Default.ErrorOutline
        }
        VentlySnackbarType.SUCCESS -> {
            accentColor = Success
            bgColor = SuccessBg.copy(alpha = 0.97f)
            textColor = Color(0xFF065F46)
            icon = Icons.Default.CheckCircle
        }
        VentlySnackbarType.INFO -> {
            accentColor = Primary
            bgColor = PrimaryFixed.copy(alpha = 0.97f)
            textColor = OnPrimaryContainer
            icon = Icons.Default.Info
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .drawBehind {
                    drawRect(
                        color = accentColor,
                        topLeft = Offset.Zero,
                        size = size.copy(width = 4.dp.toPx()),
                    )
                }
                .padding(start = 20.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = data.visuals.message,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = { data.dismiss() },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

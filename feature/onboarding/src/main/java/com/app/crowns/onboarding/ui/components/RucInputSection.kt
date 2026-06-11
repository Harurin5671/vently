package com.app.crowns.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.ui.components.VentlyInfoBanner

@Composable
fun RucInputSection(
    ruc: String,
    businessName: String,
    onChanged: (String) -> Unit,
    isValid: Boolean,
    isSearching: Boolean,
    searchError: String? = null,
) {
    Column {
        // Label + contador
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "RUC",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
            Text(
                text = "${ruc.length}/11",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .border(
                    width = 1.5.dp,
                    color = when {
                        searchError != null -> Error
                        isValid -> Success
                        else -> OutlineVariant
                    },
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = ruc,
                onValueChange = { if (it.length <= 11) onChanged(it) },
                enabled = !isSearching,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Spacer izquierdo para balancear el ícono derecho
                        Spacer(modifier = Modifier.size(24.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }

                        // Ícono de validación o progreso
                        Box(modifier = Modifier.size(24.dp)) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = PrimaryContainer,
                                    strokeWidth = 2.dp
                                )
                            } else if (isValid) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "RUC válido",
                                    tint = Success,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        if (isSearching) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Buscando información...",
                fontSize = 12.sp,
                color = PrimaryContainer
            )
        } else if (searchError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            VentlyInfoBanner(message = searchError)
        } else if (isValid && businessName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            // Feedback de validación
            Text(
                text = "✓  RUC válido — $businessName",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Success
            )
        }
    }
}
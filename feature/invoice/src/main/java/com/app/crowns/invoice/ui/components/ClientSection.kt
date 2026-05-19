package com.app.crowns.invoice.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.Success
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.invoice.domain.model.InvoiceType

@Composable
fun ClientSection(
    invoiceType: InvoiceType,
    clientName: String,
    clientRuc: String,
    clientBusinessName: String,
    isSearchingRuc: Boolean,
    rucError: String?,
    onClientNameChanged: (String) -> Unit,
    onClientRucChanged: (String) -> Unit,
    onSearchRuc: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (invoiceType) {
            InvoiceType.BOLETA -> {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = onClientNameChanged,
                    placeholder = { Text("Cliente general (opcional)", color = Outline) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = OutlineVariant,
                    ),
                )
            }

            InvoiceType.FACTURA -> {
                // RUC + search button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = clientRuc,
                            onValueChange = onClientRucChanged,
                            enabled = !isSearchingRuc,
                            placeholder = { Text("20XXXXXXXXX", color = Outline) },
                            isError = rucError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                if (clientBusinessName.isNotBlank()) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Success,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (clientBusinessName.isNotBlank()) Success else Primary,
                                unfocusedBorderColor = if (clientBusinessName.isNotBlank()) Success else OutlineVariant,
                                errorBorderColor = Color(0xFFEF4444),
                            ),
                        )
                        rucError?.let {
                            Text(
                                text = it,
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                            )
                        }
                        if (clientBusinessName.isNotBlank()) {
                            Text(
                                text = "RUC válido",
                                fontSize = 11.sp,
                                color = Success,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }

                    Button(
                        onClick = onSearchRuc,
                        enabled = clientRuc.length == 11 && !isSearchingRuc,
                        modifier = Modifier
                            .height(56.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary,
                        ),
                    ) {
                        if (isSearchingRuc) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = OnPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Buscar",
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                // Business name readonly
                if (clientBusinessName.isNotBlank()) {
                    Column {
                        OutlinedTextField(
                            value = clientBusinessName,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = OutlineVariant,
                                unfocusedBorderColor = OutlineVariant,
                            ),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Autocompletado desde SUNAT",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
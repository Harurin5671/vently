package com.app.crowns.invoice.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.invoice.domain.model.InvoiceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSection(
    invoiceType: InvoiceType,
    clientName: String,
    clientRuc: String,
    clientBusinessName: String,
    isSearchingRuc: Boolean,
    rucError: String?,
    clientNameManualFallback: Boolean = false,
    total: Double = 0.0,
    documentTypes: List<IdentityDocumentTypeEntity> = emptyList(),
    selectedDocumentType: IdentityDocumentTypeEntity? = null,
    onDocumentTypeChanged: (IdentityDocumentTypeEntity) -> Unit = {},
    onClientNameChanged: (String) -> Unit,
    onClientRucChanged: (String) -> Unit,
    onSearchRuc: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (invoiceType) {
            InvoiceType.BOLETA -> {
                // Document Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedDocumentType?.name ?: "DNI",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de documento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        documentTypes.filter { type ->
                            // En Boleta mostramos "Sin documento" solo si el total es < 700
                            if (type.isNone) total < 700.0 else true
                        }.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    onDocumentTypeChanged(type)
                                    expanded = false
                                },
                            )
                        }
                    }
                }

                // Document Number + Search (if RUC or DNI is selected)
                if (selectedDocumentType?.isRuc == true || selectedDocumentType?.isDni == true) {
                    val isRuc = selectedDocumentType.isRuc
                    val label = if (isRuc) "RUC *" else "DNI *"
                    val placeholder = if (isRuc) "20XXXXXXXXX" else "XXXXXXXX"
                    val searchEnabled = if (isRuc) clientRuc.length == 11 else clientRuc.length == 8
                    val hasValue = if (isRuc) clientBusinessName.isNotBlank() else clientName.isNotBlank()

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = clientRuc,
                                onValueChange = onClientRucChanged,
                                enabled = !isSearchingRuc,
                                label = { Text(label) },
                                placeholder = { Text(placeholder, color = Outline) },
                                isError = rucError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = {
                                    if (hasValue) {
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
                                    focusedBorderColor = if (hasValue) Success else Primary,
                                    unfocusedBorderColor = if (hasValue) Success else OutlineVariant,
                                    errorBorderColor = ErrorBright,
                                ),
                            )
                            rucError?.let {
                                Text(
                                    text = it,
                                    fontSize = 11.sp,
                                    color = ErrorBright,
                                )
                            }
                        }

                        Button(
                            onClick = onSearchRuc,
                            enabled = searchEnabled && !isSearchingRuc,
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
                                Text("Buscar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else if (selectedDocumentType?.isNone == false) {
                    OutlinedTextField(
                        value = clientRuc,
                        onValueChange = onClientRucChanged,
                        label = { Text("Número de documento *") },
                        placeholder = { Text("Obligatorio", color = Outline) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                    )
                }

                // Business Name / Name
                if (selectedDocumentType?.isNone == false) {
                    val isRuc = selectedDocumentType.isRuc
                    val isDni = selectedDocumentType.isDni
                    val label = if (isRuc) "Razón Social *" else "Nombre / Razón Social *"

                    // El campo es editable solo cuando la búsqueda falló (fallback manual).
                    // En búsqueda exitosa permanece read-only para no alterar datos del padrón.
                    val isReadOnly = (isRuc || isDni) && !clientNameManualFallback
                    val placeholder = when {
                        clientNameManualFallback -> "Ingresa el nombre manualmente"
                        isRuc || isDni -> "Autocompletado"
                        else -> "Obligatorio"
                    }
                    val borderColor = when {
                        clientNameManualFallback -> StockLow
                        else -> Primary
                    }
                    val unfocusedBorderColor = when {
                        clientNameManualFallback -> StockLow
                        else -> OutlineVariant
                    }

                    Column {
                        OutlinedTextField(
                            value = if (isRuc) clientBusinessName else clientName,
                            onValueChange = onClientNameChanged,
                            readOnly = isReadOnly,
                            enabled = !isReadOnly,
                            label = { Text(label) },
                            placeholder = { Text(placeholder, color = Outline) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = borderColor,
                                unfocusedBorderColor = unfocusedBorderColor,
                                disabledBorderColor = OutlineVariant,
                                disabledLabelColor = OnSurfaceVariant,
                                disabledTextColor = OnSurface,
                            ),
                        )
                        if (clientNameManualFallback) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "No encontrado en el padrón. Puedes ingresar el nombre para continuar.",
                                fontSize = 11.sp,
                                color = StockLow,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                } else {
                    // Si es "Sin documento", mostramos un mensaje informativo
                    Text(
                        text = "Boleta anónima permitida para montos menores a S/ 700.00",
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
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
                                errorBorderColor = ErrorBright,
                            ),
                        )
                        rucError?.let {
                            Text(
                                text = it,
                                fontSize = 11.sp,
                                color = ErrorBright,
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

                // Business name: read-only si vino de SUNAT, editable si la búsqueda falló
                if (clientBusinessName.isNotBlank() || clientNameManualFallback) {
                    Column {
                        OutlinedTextField(
                            value = clientBusinessName,
                            onValueChange = onClientNameChanged,
                            readOnly = !clientNameManualFallback,
                            enabled = clientNameManualFallback,
                            placeholder = if (clientNameManualFallback) {
                                { Text("Ingresa la razón social manualmente", color = Outline) }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (clientNameManualFallback) StockLow else Primary,
                                unfocusedBorderColor = if (clientNameManualFallback) StockLow else OutlineVariant,
                                disabledBorderColor = OutlineVariant,
                                disabledTextColor = OnSurface,
                            ),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (clientNameManualFallback)
                                "No encontrado en SUNAT. Puedes ingresar la razón social para continuar."
                            else
                                "Autocompletado desde SUNAT",
                            fontSize = 11.sp,
                            color = if (clientNameManualFallback) StockLow else OnSurfaceVariant,
                            fontWeight = if (clientNameManualFallback) FontWeight.Medium else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}